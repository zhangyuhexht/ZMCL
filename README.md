# ZMCL

> **非官方修改版 / Unofficial modified build**
>
> ZMCL 是基于 [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2) 二次开发的 Android Minecraft Java 版启动器，与 Zalith Launcher、PojavLauncher、Mojang 或 Microsoft 均无隶属关系。

ZMCL（ZYH Minecraft Launcher）是一个面向 Android 的 Minecraft Java 版启动器，使用 Jetpack Compose 与 Material 3 构建。项目提供深色响应式界面、四档界面动效、版本与模组管理、账户管理、JRE 下载、OpenGL/Vulkan 渲染以及虚拟触控布局。

当前版本：**1.20.1**（`versionCode 100019`）

## 功能

- **版本与加载器**：原版 Minecraft、Forge、NeoForge、Fabric、Quilt、Legacy Fabric、OptiFine、Cleanroom、Babric 等。
- **整合包与资源**：CurseForge、Modrinth、MCBBS、MultiMC 整合包导入；模组、资源包、光影、存档和服务器管理。
- **账户**：Microsoft 账户、外置 Yggdrasil 认证服务器、离线账户、皮肤与披风。
- **运行时**：JRE 8 / 17 / 21 / 25，按游戏与加载器版本选择 Java。
- **渲染**：NG-GL4ES、GL4ES、Vulkan/Zink、VirGL、Freedreno、Panfrost，并支持渲染器插件。
- **操作**：虚拟鼠标、手柄、陀螺仪、自定义触控布局、输入法桥和快捷物品栏。
- **联机**：基于 Terracotta / EasyTier 的局域网联机。
- **界面**：Material 3 Expressive 深色主题、可切换主题色、完整/标准/精简/关闭四档动效、响应式手机与平板布局。
- **安全存储**：账户与认证服务器数据库使用 SQLCipher 加密，口令由 Android Keystore 包装；数据库和偏好设置不进入系统备份。

## 下载

前往 [Releases](https://github.com/zhangyuhexht/ZMCL/releases/latest) 下载最新 APK。

当前公开产物为 **debug 签名预览包**，包名带 `.v2.debug` 后缀，可能与正式版共存。正式发布请使用你自己的签名密钥构建 `release` 变体。

系统要求：

- Android 10（API 29）或更高
- `arm64-v8a`、`armeabi-v7a`、`x86` 或 `x86_64`

## 构建

构建环境：

| 项目 | 版本 |
| --- | --- |
| JDK | 17 |
| Gradle | 9.4.1（Wrapper） |
| compileSdk | 37 |
| targetSdk | 35 |
| minSdk | 29 |
| NDK | 25.2.9519653 |

```powershell
# Debug
.\gradlew.bat :ZMCL:assembleDebug

# Release（按 ABI）
.\gradlew.bat :ZMCL:assembleRelease -Darch=arm64
```

`-Darch` 可取 `all`、`arm`、`arm64`、`x86`、`x86_64`。应用模块目录仍为 `ZalithLauncher/`，Gradle 模块名为 `:ZMCL`。

### Release 签名

签名材料不进入仓库。构建前设置以下环境变量（或写入本机 `gradle.properties`，不要提交）：

```text
RELEASE_STORE_FILE=<keystore 绝对路径>
RELEASE_STORE_PASSWORD=<store password>
RELEASE_KEY_ALIAS=<key alias>
RELEASE_KEY_PASSWORD=<key password>
```

未配置签名时，`release` 变体会拒绝构建，避免误产出无签名或使用上游密钥的包。

## 安全设计

- 账户、认证服务器和游戏目录数据库使用 SQLCipher 加密。
- 数据库口令为随机生成，并由 Android Keystore 中的不可导出密钥包装。
- 从旧版明文数据库升级时，会先导出到加密临时库、校验页数与完整性，再替换原库。
- `database`、`sharedpref` 和 `file` 域均排除在云备份与设备迁移之外。
- Release 包默认禁止明文 HTTP；仅 `localhost`、`127.0.0.1` 和 Android 模拟器宿主地址保留开发例外。
- Debug 包允许明文 HTTP，便于本地调试。

## 账户策略

当前版本沿用上游账户策略：在大中华区以外，离线账户和外置账户需要先添加过一个 Microsoft 账户。该限制生效时，账户界面会显示说明。

## 项目结构

| 模块 | 职责 |
| --- | --- |
| `:ZMCL`（目录 `ZalithLauncher/`） | Android 主应用：启动流程、下载安装、账户、JVM、UI |
| `:LayerController` | 触控布局、控件模型、编辑器与多点触控处理 |
| `:ColorPicker` | Compose HSV/透明度颜色选择器 |
| `:Terracotta` | 基于 EasyTier / VPN 的虚拟局域网联机后端 |
| `:LWJGL` | 生成游戏 JVM 使用的 LWJGL / GLFW fat JAR |

```text
ZalithLauncher/src/main/java/com/zhangyuhexht/zmcl/
├── game/       版本、下载、账户、JRE、启动、渲染、联机
├── ui/         Compose 页面、组件、主题、设置
├── setting/    设置注册表与持久化
├── database/   Room + SQLCipher
├── bridge/     JNI / 原生桥
└── utils/      网络、日志、文件、动画等工具
```

## 上游与许可证

本项目基于 [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2) `2.4.7` 修改而来，并保留上游版权声明与许可证。

- 许可证：[GNU GPLv3](LICENSE)
- 上游附加条款要求修改版在启动页或主界面明显标注“非官方修改版”；ZMCL 在启动页顶部显示该标识。
- Minecraft 是 Mojang Studios / Microsoft 的商标。本项目不附带游戏本体，使用前需要自备正版账户与游戏文件。

## 致谢

感谢 Zalith Launcher、PojavLauncher、LWJGL、Terracotta、EasyTier 以及所有上游开源项目的贡献者。

---

## English summary

ZMCL is an unofficial Android launcher for Minecraft: Java Edition, based on Zalith Launcher 2. It is not affiliated with Zalith Launcher, PojavLauncher, Mojang, or Microsoft. The project provides version/modpack management, Microsoft and third-party accounts, JRE management, OpenGL/Vulkan renderers, custom touch controls, and Terracotta LAN play. The latest preview APK is available in [Releases](https://github.com/zhangyuhexht/ZMCL/releases/latest). Licensed under GPLv3; see [LICENSE](LICENSE).
