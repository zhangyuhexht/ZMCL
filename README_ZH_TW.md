# ZMCL

ZYH Minecraft Launcher（簡稱 ZMCL）是一個面向 Android 的 Minecraft Java 版啟動器，由 `zhangyuhexht` 維護。專案使用 Jetpack Compose 與 Material 3，提供深色、響應式的啟動器介面。

## 目前原始碼

倉庫根目錄即完整原始碼。為了保持現有 Gradle 建置相容性，應用模組目錄仍然是 `ZalithLauncher/`，Gradle 模組名稱為 `:ZMCL`，應用套件、應用身份與所有使用者可見品牌均為 ZMCL。目前啟動器版本為 `1.20.1`（`versionCode=100019`）。

目前介面重點包括：

- 基於 Material 3 的深色主題與可切換主題色
- 完整、標準、精簡、關閉四檔動畫效果
- 包含語言、主題色、動畫、圖示與帳戶步驟的開箱流程
- 適配手機、平板與桌面視窗的響應式橫向佈局
- 簡體中文、繁體中文、英文（美國）與英文（英國）

## 建置

使用 Android Studio 開啟倉庫根目錄。目前建置環境為 JDK 17、Gradle 9.4.1、`compileSdk=37`、`targetSdk=35`、`minSdk=29` 與 NDK `25.2.9519653`。

```powershell
.\gradlew.bat :ZMCL:assembleDebug
.\gradlew.bat :ZMCL:assembleRelease -Darch=arm64
```

`-Darch` 可為 `all`、`arm`、`arm64`、`x86` 或 `x86_64`。需要發佈 APK 時，建置產物統一放在原始碼目錄外的 `build apk/` 資料夾。

Release 簽章資料不再保存在儲存庫中。建置前需設定 `RELEASE_STORE_FILE`、`RELEASE_STORE_PASSWORD`、`RELEASE_KEY_ALIAS` 與 `RELEASE_KEY_PASSWORD`，詳見 [SECURITY.md](SECURITY.md)。

## 安全與帳戶策略

- 帳戶資料使用 SQLCipher 加密，資料庫口令由 Android Keystore 包裝。
- 資料庫、SharedPreferences 與內部檔案不會進入 Android 雲端備份或裝置移轉備份。
- Release 預設禁止明文 HTTP，只保留本機開發位址例外；Debug 允許明文 HTTP 以便除錯。
- 在大中華區以外，離線與外置帳戶需要先新增過 Microsoft 帳戶；帳戶畫面會在限制生效時顯示說明。

本專案基於 Zalith Launcher 2 的 `2.4.7` 版本修改而來。上游專案儲存庫為 [ZalithLauncher/ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2)。專案保留上游授權聲明與必要的版權歸屬，ZMCL 的介面、品牌與功能修改以 ZMCL 名義獨立維護。

仓库地址：[zhangyuhexht/ZMCL](https://github.com/zhangyuhexht/ZMCL)

## 專案資訊

- 產品名稱：ZYH Minecraft Launcher
- 簡稱：ZMCL
- 作者：zhangyuhexht
- 授權：GNU GPLv3

## 開發約定

介面修改應放在 Compose 頁面與元件層，統一使用共享的 Material 3 色彩系統與動畫設定，避免引入第二套動畫系統。新增字串必須同步維護四種支援的語言資源；長文字應使用有邊界的佈局，並依情境使用省略或滾動，避免重疊與溢出。
