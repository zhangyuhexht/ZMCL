/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.ui.theme

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.zhangyuhexht.zmcl.R

enum class AccentColor(
    @StringRes val labelRes: Int,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val onSecondary: Color
) {
    RED(R.string.accent_color_red, Color(0xFFFF8A80), Color(0xFF8C1D18), Color(0xFF3B0905), Color(0xFFFFB4AB), Color(0xFF5F1410), Color(0xFF3B0905)),
    YELLOW(R.string.accent_color_yellow, Color(0xFFFFD966), Color(0xFF6F5300), Color(0xFF2A2000), Color(0xFFFFB95C), Color(0xFF5A3A00), Color(0xFF2A1800)),
    GREEN(R.string.accent_color_green, Color(0xFF81C995), Color(0xFF1E5F31), Color(0xFF06210D), Color(0xFFA5D6A7), Color(0xFF245B2B), Color(0xFF09200D)),
    PINK(R.string.accent_color_pink, Color(0xFFFF8FB1), Color(0xFF7A2948), Color(0xFF350014), Color(0xFFFFB1C8), Color(0xFF69213D), Color(0xFF350014)),
    ORANGE(R.string.accent_color_orange, Color(0xFFFFB77A), Color(0xFF713A00), Color(0xFF2E1600), Color(0xFFFFC28A), Color(0xFF633300), Color(0xFF2E1600)),
    PURPLE(R.string.accent_color_purple, Color(0xFFD0BCFF), Color(0xFF4F378B), Color(0xFF25105C), Color(0xFFE8DEF8), Color(0xFF4A4458), Color(0xFF211B24)),
    CYAN(R.string.accent_color_cyan, Color(0xFF8FD9FF), Color(0xFF004C6D), Color(0xFF00344D), Color(0xFF9FD9E7), Color(0xFF164E5C), Color(0xFF002B33)),
    WHITE(R.string.accent_color_white, Color(0xFFF3F4F6), Color(0xFF4A4D52), Color(0xFF202124), Color(0xFFD0D3D8), Color(0xFF414449), Color(0xFF202124))
}
