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

package com.zhangyuhexht.zmcl.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.utils.animation.MotionLevel

@Composable
fun Modifier.infiniteShimmer(
    initialValue: Float = 0.3f,
    targetValue: Float = 0.6f
): Modifier {
    val motionLevel = AllSettings.launcherMotionLevel.state
    if (motionLevel == MotionLevel.OFF) {
        return this.then(Modifier.graphicsLayer { alpha = targetValue })
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loading-shimmer")
    val durationMillis = when (motionLevel) {
        MotionLevel.FULL -> 900
        MotionLevel.STANDARD -> 1200
        MotionLevel.REDUCED -> 1600
        MotionLevel.OFF -> 0
    }

    //循环动画
    val animatedAlpha = infiniteTransition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading-shimmer-alpha"
    )

    return this.then(
        Modifier.graphicsLayer {
            alpha = animatedAlpha.value
        }
    )
}

/**
 * 无限循环闪烁Box，可用于制作加载时骨架
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    shape: Shape = RectangleShape
) {


    Box(
        modifier = modifier
            .infiniteShimmer()
            .background(
                color = color,
                shape = shape
            )
    )
}
