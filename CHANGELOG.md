# Changelog

## 1.20.1

- Encrypt the account, authentication-server, and game-path database with
  SQLCipher and Android Keystore.
- Migrate existing plaintext databases without discarding account records.
- Exclude databases, SharedPreferences, and internal files from Android backup.
- Move release signing material and passwords outside the repository.
- Require explicit release signing configuration and fail closed when it is
  missing.
- Fix the CI Gradle module path and run a clean build for each artifact.
- Correct the GLFW focus attribute mapping for game window focus changes.
- Surface the existing external-account region policy in the account UI.
- Reject public cleartext HTTP authentication servers in release builds.
- Update build documentation for JDK 17, Gradle 9.4.1, API 29/35/37, and NDK
  25.2.9519653.
