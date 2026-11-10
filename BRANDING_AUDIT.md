# ZMCL branding compatibility audit

The application namespace, package, visible name, runtime environment key, UI strings, primary source packages, and test source paths use ZMCL.

The following upstream identifiers are intentionally retained:

- `LICENSE` and upstream source copyright headers: required attribution for GPL-derived code.
- `ZalithLauncher/`: physical Gradle module directory retained to avoid breaking native/LWJGL asset paths; Gradle exposes it as `:ZMCL`.
- `zalith_launcher*.jks` and `movtery_zalith*`: signing material retained so existing installations remain upgrade-compatible.
- `com.movtery.layer_controller`, `com.movtery.colorpicker`, and `com.movtery.buildkeys`: internal library namespaces, not user-visible product branding.
- `[zl_version]`: legacy imported-profile placeholder retained beside `[zmcl_version]` for backward compatibility.
- `com.movtery.activity.*`: manifest aliases retained for external file-import intents used by existing integrations.

Do not rename these items without a migration for signatures, serialized data, native paths, and external intents.
