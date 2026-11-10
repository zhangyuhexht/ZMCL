/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.setting.enums

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import com.zhangyuhexht.zmcl.R

enum class LauncherIcon(
    @StringRes val labelRes: Int,
    val aliasClassName: String,
    @DrawableRes val previewForegroundRes: Int,
    @DrawableRes val previewBackgroundRes: Int? = null,
    val enabledByDefault: Boolean = false
) {
    WHITE(
        R.string.launcher_icon_white,
        "com.zhangyuhexht.zmcl.icon.WhiteLauncherAlias",
        R.drawable.img_launcher_zmcl,
        enabledByDefault = true
    ),
    BLACK(
        R.string.launcher_icon_black,
        "com.zhangyuhexht.zmcl.icon.BlackLauncherAlias",
        R.drawable.img_launcher_zmcl_black
    ),
    CHERRY(
        R.string.launcher_icon_cherry,
        "com.zhangyuhexht.zmcl.icon.CherryLauncherAlias",
        R.drawable.ic_launcher_cherry_foreground,
        R.drawable.img_launcher_cherry_bg
    ),
    MINECRAFT_1_21_8(
        R.string.launcher_icon_minecraft_1_21_8,
        "com.zhangyuhexht.zmcl.icon.Minecraft1218LauncherAlias",
        R.drawable.ic_launcher_cherry_foreground,
        R.drawable.img_launcher_mc_1_21_8_bg
    ),
    MINECRAFT_1_21_4(
        R.string.launcher_icon_minecraft_1_21_4,
        "com.zhangyuhexht.zmcl.icon.Minecraft1214LauncherAlias",
        R.drawable.ic_launcher_cherry_foreground,
        R.drawable.img_launcher_mc_1_21_4_bg
    ),
    MINECRAFT_1_21_1(
        R.string.launcher_icon_minecraft_1_21_1,
        "com.zhangyuhexht.zmcl.icon.Minecraft1211LauncherAlias",
        R.drawable.ic_launcher_cherry_foreground,
        R.drawable.img_launcher_mc_1_21_1_bg
    ),
    MINECRAFT_1_20_1(
        R.string.launcher_icon_minecraft_1_20_1,
        "com.zhangyuhexht.zmcl.icon.Minecraft1201LauncherAlias",
        R.drawable.ic_launcher_cherry_foreground,
        R.drawable.img_launcher_mc_1_20_1_bg
    ),
    MINECRAFT_1_12(
        R.string.launcher_icon_minecraft_1_12,
        "com.zhangyuhexht.zmcl.icon.Minecraft112LauncherAlias",
        R.drawable.ic_launcher_cherry_foreground,
        R.drawable.img_launcher_mc_1_12_bg
    )
}
