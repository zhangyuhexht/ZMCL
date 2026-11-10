/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.zhangyuhexht.zmcl.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zhangyuhexht.zmcl.game.account.Account
import com.zhangyuhexht.zmcl.game.account.AccountDao
import com.zhangyuhexht.zmcl.game.account.auth_server.data.AuthServer
import com.zhangyuhexht.zmcl.game.account.auth_server.data.AuthServerDao
import com.zhangyuhexht.zmcl.game.path.GamePath
import com.zhangyuhexht.zmcl.game.path.GamePathDao
import net.zetetic.database.sqlcipher.SQLiteDatabase as SqlCipherDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets

@Database(
    entities = [Account::class, AuthServer::class, GamePath::class],
    version = 2,
    exportSchema = false //默认不支持导出
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * 启动器账号
     */
    abstract fun accountDao(): AccountDao

    /**
     * 认证服务器
     */
    abstract fun authServerDao(): AuthServerDao

    /**
     * 游戏目录
     */
    abstract fun gamePathDao(): GamePathDao

    companion object {
        private const val TAG = "AppDatabase"
        private const val DATABASE_NAME = "launcher_data.db"
        private const val SQLITE_HEADER = "SQLite format 3\u0000"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN expiresAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * 获取全局数据库实例
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                System.loadLibrary("sqlcipher")
                val appContext = context.applicationContext
                val passphrase = DatabaseKeyManager.getOrCreatePassphrase(appContext)
                migrateLegacyPlaintextDatabase(appContext, passphrase)

                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .openHelperFactory(SupportOpenHelperFactory(passphrase))
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Existing installations used a plaintext Room database. SQLCipher can
         * export that database into a newly keyed file while preserving tables,
         * indexes, rows and Room's user_version.
         */
        private fun migrateLegacyPlaintextDatabase(
            context: Context,
            passphrase: ByteArray
        ) {
            val databaseFile = context.getDatabasePath(DATABASE_NAME)
            if (!databaseFile.exists() || !hasPlaintextSqliteHeader(databaseFile)) {
                return
            }

            val encryptedFile = File(databaseFile.parentFile, "$DATABASE_NAME.encrypted.tmp")
            val backupFile = File(databaseFile.parentFile, "$DATABASE_NAME.migration-backup")
            deleteDatabaseFiles(encryptedFile)
            deleteDatabaseFiles(backupFile)

            Log.i(TAG, "Migrating the legacy plaintext launcher database to SQLCipher.")
            val plaintextPassword: String? = null
            val source = SqlCipherDatabase.openDatabase(
                databaseFile.absolutePath,
                plaintextPassword,
                null,
                SqlCipherDatabase.OPEN_READWRITE,
                null
            )
            val sourceVersion = try {
                runCatching {
                    source.rawExecSQL("PRAGMA wal_checkpoint(TRUNCATE)")
                }
                readUserVersion(source)
            } finally {
                source.close()
            }

            try {
                val export = SqlCipherDatabase.openDatabase(
                    databaseFile.absolutePath,
                    plaintextPassword,
                    null,
                    SqlCipherDatabase.OPEN_READONLY,
                    null
                )
                try {
                    export.execSQL(
                        "ATTACH DATABASE ? AS zmcl_encrypted KEY ?",
                        arrayOf<Any>(encryptedFile.absolutePath, passphrase)
                    )
                    export.rawQuery(
                        "SELECT sqlcipher_export('zmcl_encrypted')",
                        emptyArray<String>()
                    ).use { cursor ->
                        while (cursor.moveToNext()) {
                            // Consume the export result.
                        }
                    }
                    export.execSQL("PRAGMA zmcl_encrypted.user_version = $sourceVersion")
                    export.execSQL("DETACH DATABASE zmcl_encrypted")
                } finally {
                    export.close()
                }

                verifyEncryptedDatabase(encryptedFile, passphrase, sourceVersion)
                replaceMigratedDatabase(databaseFile, encryptedFile, backupFile)
                Log.i(TAG, "Legacy launcher database migration completed.")
            } catch (throwable: Throwable) {
                deleteDatabaseFiles(encryptedFile)
                throw IllegalStateException(
                    "Unable to migrate the launcher database to encrypted storage.",
                    throwable
                )
            }
        }

        private fun verifyEncryptedDatabase(
            databaseFile: File,
            passphrase: ByteArray,
            expectedVersion: Int
        ) {
            val database = SqlCipherDatabase.openDatabase(
                databaseFile.absolutePath,
                passphrase,
                null,
                SqlCipherDatabase.OPEN_READONLY,
                null
            )
            try {
                val integrity = database.rawQuery("PRAGMA integrity_check", emptyArray()).use {
                    if (it.moveToFirst()) it.getString(0) else null
                }
                check(integrity == "ok") {
                    "Encrypted database integrity check failed: $integrity"
                }
                check(readUserVersion(database) == expectedVersion) {
                    "Encrypted database user_version does not match the legacy database."
                }
            } finally {
                database.close()
            }
        }

        private fun replaceMigratedDatabase(
            databaseFile: File,
            encryptedFile: File,
            backupFile: File
        ) {
            deleteDatabaseFiles(backupFile)
            check(databaseFile.renameTo(backupFile)) {
                "Unable to preserve the original launcher database."
            }

            try {
                // Sidecars keep the original base name. Remove them after the
                // source connection has checkpointed and closed.
                deleteDatabaseFiles(databaseFile)
                check(encryptedFile.renameTo(databaseFile)) {
                    "Unable to install the encrypted launcher database."
                }
                deleteDatabaseFiles(backupFile)
            } catch (throwable: Throwable) {
                if (databaseFile.exists()) {
                    deleteDatabaseFiles(databaseFile)
                }
                if (backupFile.exists()) {
                    backupFile.renameTo(databaseFile)
                }
                throw throwable
            }
        }

        private fun readUserVersion(database: SqlCipherDatabase): Int {
            return database.rawQuery("PRAGMA user_version", emptyArray()).use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        }

        private fun hasPlaintextSqliteHeader(databaseFile: File): Boolean {
            if (databaseFile.length() < SQLITE_HEADER.length) return false
            val expected = SQLITE_HEADER.toByteArray(StandardCharsets.UTF_8)
            val actual = ByteArray(expected.size)
            return FileInputStream(databaseFile).use { input ->
                input.read(actual) == expected.size &&
                    actual.contentEquals(expected)
            }
        }

        private fun deleteDatabaseFiles(databaseFile: File) {
            listOf(
                databaseFile,
                File("${databaseFile.absolutePath}-wal"),
                File("${databaseFile.absolutePath}-shm"),
                File("${databaseFile.absolutePath}-journal")
            ).forEach { file ->
                if (file.exists() && !file.delete()) {
                    throw IllegalStateException(
                        "Unable to remove database file: ${file.absolutePath}"
                    )
                }
            }
        }
    }
}
