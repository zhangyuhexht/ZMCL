/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Protects the SQLCipher passphrase with a non-exportable Android Keystore key.
 *
 * The encrypted passphrase is intentionally stored outside the Room database and
 * both SharedPreferences and databases are excluded from Android backups.
 */
internal object DatabaseKeyManager {
    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "zmcl_database_key_v1"
    private const val PREFERENCES = "zmcl_database_security"
    private const val PREFERENCE_IV = "passphrase_iv"
    private const val PREFERENCE_PAYLOAD = "passphrase_payload"
    private const val PASSPHRASE_SIZE_BYTES = 32
    private const val GCM_TAG_BITS = 128

    @Synchronized
    fun getOrCreatePassphrase(context: Context): ByteArray {
        val preferences = context.applicationContext
            .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val encodedIv = preferences.getString(PREFERENCE_IV, null)
        val encodedPayload = preferences.getString(PREFERENCE_PAYLOAD, null)

        if (encodedIv != null || encodedPayload != null) {
            check(encodedIv != null && encodedPayload != null) {
                "The encrypted database key record is incomplete."
            }
            return decryptPassphrase(
                key = getOrCreateKeystoreKey(),
                iv = Base64.decode(encodedIv, Base64.NO_WRAP),
                payload = Base64.decode(encodedPayload, Base64.NO_WRAP)
            )
        }

        val passphrase = ByteArray(PASSPHRASE_SIZE_BYTES).also {
            SecureRandom().nextBytes(it)
        }
        val key = getOrCreateKeystoreKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val payload = cipher.doFinal(passphrase)

        val persisted = preferences.edit()
            .putString(PREFERENCE_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString(PREFERENCE_PAYLOAD, Base64.encodeToString(payload, Base64.NO_WRAP))
            .commit()
        check(persisted) { "Unable to persist the encrypted database key." }
        return passphrase
    }

    private fun getOrCreateKeystoreKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private fun decryptPassphrase(
        key: SecretKey,
        iv: ByteArray,
        payload: ByteArray
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(payload)
    }
}
