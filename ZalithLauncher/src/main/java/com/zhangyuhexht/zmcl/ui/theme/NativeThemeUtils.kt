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

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

data class NativeColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val surfaceDim: Color,
    val surfaceBright: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
)

private fun getColorScheme(): NativeColorScheme {
    val colors = currentGoogleMaterialDarkColorScheme()
    return NativeColorScheme(
        primary = colors.primary,
        onPrimary = colors.onPrimary,
        primaryContainer = colors.primaryContainer,
        onPrimaryContainer = colors.onPrimaryContainer,
        secondary = colors.secondary,
        onSecondary = colors.onSecondary,
        secondaryContainer = colors.secondaryContainer,
        onSecondaryContainer = colors.onSecondaryContainer,
        tertiary = colors.tertiary,
        onTertiary = colors.onTertiary,
        tertiaryContainer = colors.tertiaryContainer,
        onTertiaryContainer = colors.onTertiaryContainer,
        error = colors.error,
        onError = colors.onError,
        errorContainer = colors.errorContainer,
        onErrorContainer = colors.onErrorContainer,
        background = colors.background,
        onBackground = colors.onBackground,
        surface = colors.surface,
        onSurface = colors.onSurface,
        surfaceVariant = colors.surfaceVariant,
        onSurfaceVariant = colors.onSurfaceVariant,
        outline = colors.outline,
        outlineVariant = colors.outlineVariant,
        inverseSurface = colors.inverseSurface,
        inverseOnSurface = colors.inverseOnSurface,
        inversePrimary = colors.inversePrimary,
        surfaceDim = colors.surfaceDim,
        surfaceBright = colors.surfaceBright,
        surfaceContainerLowest = colors.surfaceContainerLowest,
        surfaceContainerLow = colors.surfaceContainerLow,
        surfaceContainer = colors.surfaceContainer,
        surfaceContainerHigh = colors.surfaceContainerHigh,
        surfaceContainerHighest = colors.surfaceContainerHighest,
    )
}

fun MaterialAlertDialogBuilder.showThemed(): AlertDialog {
    val colors = getColorScheme()
    background?.setTint(colors.surfaceContainerHigh.toArgb())

    fun Button.applyThemeColors(
        colors: NativeColorScheme,
    ) {
        val contentColor = colors.primary.toArgb()
        val rippleColor = colors.secondaryContainer.copy(alpha = 0.5f).toArgb()

        this@applyThemeColors.setTextColor(contentColor)
        if (this is MaterialButton) {
            this@applyThemeColors.rippleColor = ColorStateList.valueOf(rippleColor)
        } else {
            (this@applyThemeColors.background as? RippleDrawable)?.setColor(ColorStateList.valueOf(rippleColor))
        }
    }

    return create().apply {
        setOnShowListener {
            findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(colors.onSurface.toArgb())
            findViewById<TextView>(android.R.id.message)?.setTextColor(colors.onSurfaceVariant.toArgb())
            getButton(AlertDialog.BUTTON_POSITIVE)?.applyThemeColors(colors)
            getButton(AlertDialog.BUTTON_NEGATIVE)?.applyThemeColors(colors)
            getButton(AlertDialog.BUTTON_NEUTRAL)?.applyThemeColors(colors)
        }
    }.also { it.show() }
}
