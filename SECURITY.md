# ZMCL security notes

## Account database

ZMCL stores accounts, authentication servers, and game paths in `launcher_data.db`.
The database is opened through SQLCipher. A random 32-byte database passphrase is
wrapped with a non-exportable AES-256-GCM key in Android Keystore. The wrapped
passphrase is stored separately from the database.

The first launch after upgrading from a plaintext database performs a one-time
migration:

1. Read the legacy SQLite database without a key.
2. Export it into a temporary SQLCipher database.
3. Verify `PRAGMA integrity_check` and `PRAGMA user_version`.
4. Atomically replace the plaintext database and remove legacy sidecars.

If migration fails, the original plaintext database is preserved and startup is
aborted instead of silently creating an empty account database.

## Android backup

`backup_rules.xml` and `data_extraction_rules.xml` exclude databases,
SharedPreferences, and internal files from cloud backup and device transfer.
This prevents account tokens and authentication server credentials from leaving
the device through Android backup.

## Release signing

Keystores and passwords must not be committed. Configure release signing through
environment variables:

- `RELEASE_STORE_FILE`
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

The build refuses to run a task containing `release` unless all four values are
available. Generate a new keystore outside the repository:

```powershell
keytool -genkeypair -v -keystore "$env:USERPROFILE\.zmcl-signing\release.jks" `
  -alias zmcl -keyalg RSA -keysize 4096 -validity 10000
```

After changing the release key, existing installations cannot be upgraded in
place with the new APK. This is expected when the former key must be treated as
compromised.

GitHub Actions expects a base64-encoded keystore in `SIGNING_KEYSTORE_BASE64`
and the password/alias values above as repository secrets.

## Cleartext HTTP

Release builds use a network security configuration that rejects cleartext HTTP.
The only exceptions are `localhost`, `127.0.0.1`, `::1`, and the Android emulator
host `10.0.2.2`. Authentication server URLs using public plaintext HTTP are
rejected before network access. Debug builds override the policy so local
development can use HTTP.

## Account region policy

The upstream policy is retained: outside Greater China, the launcher requires a
previously added Microsoft account before offline or third-party accounts are
made available. The account screen now explains this restriction instead of
silently showing no current account.
