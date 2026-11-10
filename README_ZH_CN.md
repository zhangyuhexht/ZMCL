# ZMCL

ZYH Minecraft Launcher（简称 ZMCL）是一个面向 Android 的 Minecraft Java 版启动器，由 `zhangyuhexht` 维护。项目使用 Jetpack Compose 和 Material 3，提供深色、响应式的启动器界面。

## 当前源码

当前滚动开发源码位于 `debug1.12` 文件夹。为了保持现有 Gradle 构建兼容性，应用模块目录仍然是 `ZalithLauncher/`，但 Gradle 模块名为 `:ZMCL`，应用包名、应用身份和所有用户可见品牌均为 ZMCL。当前启动器版本为 `1.20.1`（`versionCode=100019`）。

当前界面重点包括：

- 基于 Material 3 的深色主题和可切换主题色
- 完整、标准、精简、关闭四档动画效果
- 包含语言、主题色、动画、图标和账户步骤的开箱流程
- 适配手机、平板和桌面窗口的响应式横屏布局
- 简体中文、繁体中文、英文（美国）和英文（英国）

## 构建

使用 Android Studio 打开 `debug1.12/`。构建环境以 Gradle 配置为准：

- JDK 17（Gradle Daemon 可使用 JDK 21）
- Gradle 9.4.1
- Android SDK：`compileSdk=37`、`targetSdk=35`、`minSdk=29`
- Android NDK：`25.2.9519653`

命令行构建示例：

```powershell
.\gradlew.bat :ZMCL:assembleDebug
.\gradlew.bat :ZMCL:assembleRelease -Darch=arm64
```

`-Darch` 可取 `all`、`arm`、`arm64`、`x86` 或 `x86_64`。需要发布 APK 时，构建产物统一放到源码目录外的 `build apk/` 文件夹。

Release 签名不再保存在仓库中。构建前需要配置 `RELEASE_STORE_FILE`、`RELEASE_STORE_PASSWORD`、`RELEASE_KEY_ALIAS` 和 `RELEASE_KEY_PASSWORD`，具体方式见 [SECURITY.md](SECURITY.md)。

## 安全与账户策略

- 账户、认证服务器和游戏目录数据库使用 SQLCipher 加密，数据库口令由 Android Keystore 包装。
- 数据库、SharedPreferences 和内部文件不会进入 Android 云备份或设备迁移备份。
- Release 包默认禁止明文 HTTP，只对 `localhost`、`127.0.0.1` 和 Android 模拟器宿主地址保留开发例外；Debug 包允许明文 HTTP 以方便本地调试。
- 当前版本沿用上游账户策略：在大中华区以外，离线账户和外置账户需要先添加过一个 Microsoft 账户。账户界面会在该限制生效时显示说明。

本项目基于 Zalith Launcher 2 的 `2.4.7` 版本修改而来。上游项目仓库为 [ZalithLauncher/ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2)。项目保留上游许可证声明和必要的版权归属，ZMCL 的界面、品牌和功能修改以 ZMCL 名义独立维护。

仓库地址：[zhangyuhexht/ZMCL](https://github.com/zhangyuhexht/ZMCL)

## 项目信息

- 产品名称：ZYH Minecraft Launcher
- 简称：ZMCL
- 作者：zhangyuhexht
- 许可证：GNU GPLv3

## 开发约定

界面修改应放在 Compose 页面和组件层，统一使用共享的 Material 3 色彩系统和动画设置，避免引入第二套动画系统。新增字符串必须同步维护四种支持的语言资源；长文本应使用有边界的布局，并根据场景使用省略或滚动，避免重叠和溢出。
