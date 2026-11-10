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

package com.zhangyuhexht.zmcl.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.utils.festival.Festival
import com.zhangyuhexht.zmcl.utils.festival.LocalFestivals
import com.zhangyuhexht.zmcl.viewmodel.BackgroundViewModel
import com.zhangyuhexht.zmcl.viewmodel.LocalBackgroundViewModel

private val GoogleMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val googleMaterialDark = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF121212),
    primaryContainer = Color(0xFF174EA6),
    onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = Color(0xFF81C995),
    onSecondary = Color(0xFF121212),
    secondaryContainer = Color(0xFF1E5F31),
    onSecondaryContainer = Color(0xFFCDEFD6),
    tertiary = Color(0xFFFFD966),
    onTertiary = Color(0xFF121212),
    tertiaryContainer = Color(0xFF6F5300),
    onTertiaryContainer = Color(0xFFFFF3C4),
    error = Color(0xFFF28B82),
    onError = Color(0xFF121212),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),
    onBackground = Color(0xFFF3F4F6),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = Color(0xFF1F1F1F),
    onSurfaceVariant = Color(0xFFADB5BD),
    outline = Color(0xFF6E7681),
    outlineVariant = Color(0xFF303030),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF3F4F6),
    inverseOnSurface = Color(0xFF1F2937),
    inversePrimary = Color(0xFF4285F4),
    surfaceDim = Color(0xFF121212),
    surfaceBright = Color(0xFF303030),
    surfaceContainerLowest = Color(0xFF0B0B0B),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF1F1F1F),
    surfaceContainerHigh = Color(0xFF252525),
    surfaceContainerHighest = Color(0xFF303030),
)

internal fun currentGoogleMaterialDarkColorScheme() =
    AllSettings.launcherAccentColor.state.let { accent ->
        googleMaterialDark.copy(
            primary = accent.primary,
            onPrimary = accent.onPrimary,
            primaryContainer = accent.primaryContainer,
            secondary = accent.secondary,
            onSecondary = accent.onSecondary,
            secondaryContainer = accent.secondaryContainer,
            inversePrimary = accent.primary
        )
    }

@Composable
fun ZMCLTheme(
    backgroundViewModel: BackgroundViewModel? = null,
    festivals: List<Festival> = emptyList(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalBackgroundViewModel provides backgroundViewModel,
        LocalFestivals provides festivals
    ) {
        MaterialExpressiveTheme(
            colorScheme = currentGoogleMaterialDarkColorScheme(),
            motionScheme = MotionScheme.standard(),
            shapes = GoogleMaterialShapes,
            typography = AppTypography,
            content = content
        )
    }
}
