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

package com.zhangyuhexht.zmcl.ui.base

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

abstract class FullScreenAppCompatActivity : AbstractAppCompatActivity() {
    private var fullscreenEnabled = true

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        fullscreenEnabled = initialFullscreenEnabled()
        configureWindow()
        super.onCreate(savedInstanceState)
        applyFullscreenMode()
    }

    protected open fun initialFullscreenEnabled(): Boolean = true

    @CallSuper
    override fun onPostResume() {
        super.onPostResume()
        applyFullscreenMode()
    }

    @CallSuper
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyFullscreenMode()
    }

    fun setFullscreenEnabled(enabled: Boolean) {
        if (fullscreenEnabled == enabled) {
            applyFullscreenMode()
            return
        }
        fullscreenEnabled = enabled
        applyFullscreenMode()
    }

    @Suppress("DEPRECATION")
    private fun configureWindow() {
        window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val params = window.attributes
                val newParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                } else {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                if (params.layoutInDisplayCutoutMode != newParams) {
                    params.layoutInDisplayCutoutMode = newParams
                    window.attributes = params
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val params = window.attributes
                if (params.preferredRefreshRate != 120f) {
                    params.preferredRefreshRate = 120f
                    window.attributes = params
                }
            }

            // System bars are controlled at runtime so the settings switch remains authoritative.
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    private fun applyFullscreenMode() {
        val window = window ?: return
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (fullscreenEnabled) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Composable
fun FullscreenEffect(value: Boolean) {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity() as? FullScreenAppCompatActivity
    }

    DisposableEffect(value, activity) {
        activity?.setFullscreenEnabled(value)
        onDispose { }
    }
}

@Composable
fun Modifier.applyFullscreen(value: Boolean): Modifier {
    val fullscreenModifier = Modifier.fillMaxSize()
    return then(
        if (value) fullscreenModifier
        else fullscreenModifier.windowInsetsPadding(WindowInsets.safeDrawing)
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
