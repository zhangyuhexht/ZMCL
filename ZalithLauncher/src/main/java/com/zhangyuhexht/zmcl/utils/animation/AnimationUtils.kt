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

package com.zhangyuhexht.zmcl.utils.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.setting.AllSettings

/** 获取当前四档动效对应的持续时长。 */
fun getAnimateSpeed(): Int = AllSettings.launcherMotionLevel.state.durationMillis

/** 根据动效档位缩放级联动画的延迟。 */
fun getAdjustedDelayMillis(baseDelayMillis: Int): Int {
    if (baseDelayMillis == 0) return 0
    val level = AllSettings.launcherMotionLevel.state
    if (level == MotionLevel.OFF) return 0
    return (baseDelayMillis * level.staggerScale).toInt()
}

/** 页面切换动画是否关闭。 */
fun isSwapAnimateClosed() = AllSettings.launcherMotionLevel.state == MotionLevel.OFF

/** 让分页控件也遵守全局动效开关，关闭时不走默认弹簧。 */
suspend fun PagerState.settleToPageWithMotion(page: Int) {
    if (isSwapAnimateClosed()) {
        scrollToPage(page)
    } else {
        animateScrollToPage(page)
    }
}

/** 让列表定位遵守全局动效开关，避免默认弹簧绕过动效设置。 */
suspend fun LazyListState.settleToItemWithMotion(
    index: Int,
    scrollOffset: Int = 0
) {
    if (isSwapAnimateClosed()) {
        scrollToItem(index, scrollOffset)
    } else {
        animateScrollToItem(index, scrollOffset)
    }
}

/** 统一的横向溢出文字策略，关闭或精简动效时避免无限帧回调。 */
@Composable
fun Modifier.motionAwareMarquee(): Modifier {
    val motionLevel = AllSettings.launcherMotionLevel.state
    return then(
        when (motionLevel) {
            MotionLevel.FULL -> Modifier.basicMarquee(iterations = Int.MAX_VALUE)
            MotionLevel.STANDARD -> Modifier.basicMarquee(iterations = 3)
            MotionLevel.REDUCED -> Modifier.basicMarquee(iterations = 1)
            MotionLevel.OFF -> Modifier
        }
    )
}

/** 旋转动画只更新绘制图层，不在每一帧重组图标内容。 */
@Composable
fun Modifier.motionRotation(
    targetValue: Float,
    label: String = "MotionRotation"
): Modifier {
    val rotation = animateFloatAsState(
        targetValue = targetValue,
        animationSpec = getAnimateTween(),
        label = label
    )
    return graphicsLayer { rotationZ = rotation.value }
}

/** Gives interactive surfaces immediate, motion-level-aware press feedback. */
@Composable
fun Modifier.motionPressScale(
    interactionSource: InteractionSource,
    enabled: Boolean = true,
    pressedScale: Float = 0.98f,
    label: String = "MotionPressScale"
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val motionLevel = AllSettings.launcherMotionLevel.state
    val durationMillis = when (motionLevel) {
        MotionLevel.FULL -> if (isPressed) 64 else 112
        MotionLevel.STANDARD -> if (isPressed) 56 else 96
        MotionLevel.REDUCED -> if (isPressed) 40 else 72
        MotionLevel.OFF -> 0
    }
    val scale by animateFloatAsState(
        targetValue = if (enabled && isPressed) pressedScale.coerceIn(0.9f, 1f) else 1f,
        animationSpec = if (motionLevel == MotionLevel.OFF) {
            snap()
        } else {
            tween(durationMillis = durationMillis, easing = EaseOutCubic)
        },
        label = label
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Animates an outline in the draw phase so the bordered content is not recomposed per frame. */
@Composable
fun Modifier.motionBorder(
    targetWidth: Dp,
    targetColor: Color,
    shape: Shape,
    label: String = "MotionBorder"
): Modifier {
    val width = animateDpAsState(
        targetValue = targetWidth,
        animationSpec = getAnimateTween(),
        label = "${label}Width"
    )
    val color = animateColorAsState(
        targetValue = targetColor,
        animationSpec = getAnimateTween(),
        label = "${label}Color"
    )
    return drawWithContent {
        drawContent()
        val strokeWidth = width.value.toPx()
        if (strokeWidth > 0f) {
            inset(strokeWidth / 2f) {
                drawOutline(
                    outline = shape.createOutline(size, layoutDirection, this),
                    color = color.value,
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}

/** Material selection outline built on the draw-phase border animation. */
@Composable
fun Modifier.motionSelectionBorder(
    selected: Boolean,
    color: Color,
    shape: Shape,
    selectedWidth: Dp = 2.dp,
    label: String = "MotionSelectionBorder"
): Modifier = motionBorder(
    targetWidth = if (selected) selectedWidth else 0.dp,
    targetColor = color,
    shape = shape,
    label = label
)

/** Keeps list-entry motion off the composition path and avoids a one-frame scale flash when motion is disabled. */
@Composable
fun rememberEntryScale(initialValue: Float = 0.95f): State<Float> {
    val enabled = !isSwapAnimateClosed()
    val scale = remember { Animatable(if (enabled) initialValue else 1f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            scale.animateTo(targetValue = 1f, animationSpec = getAnimateTween())
        } else {
            scale.snapTo(1f)
        }
    }
    return scale.asState()
}

fun <E> getAnimateTween(
    delayMillis: Int = 0
): FiniteAnimationSpec<E> = if (isSwapAnimateClosed()) {
    snap()
} else {
    getAnimateTween(
        durationMillis = getAnimateSpeed(),
        delayMillis = delayMillis
    )
}

fun <E> getAnimateTween(
    durationMillis: Int,
    delayMillis: Int = 0
): FiniteAnimationSpec<E> = tween(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = EaseOutCubic
)

fun <E> getAnimateTweenBounce(
    delayMillis: Int = 0
): FiniteAnimationSpec<E> = if (isSwapAnimateClosed()) {
    snap()
} else {
    getAnimateTweenBounce(
        durationMillis = getAnimateSpeed(),
        delayMillis = delayMillis
    )
}

fun <E> getAnimateTweenBounce(
    durationMillis: Int,
    delayMillis: Int = 0
): FiniteAnimationSpec<E> = tween(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = BounceEasing
)

fun <E> getAnimateTweenJellyBounce(
    delayMillis: Int = 0
): FiniteAnimationSpec<E> = if (isSwapAnimateClosed()) {
    snap()
} else {
    getAnimateTweenJellyBounce(
        durationMillis = getAnimateSpeed(),
        delayMillis = delayMillis
    )
}

fun <E> getAnimateTweenJellyBounce(
    durationMillis: Int,
    delayMillis: Int = 0
): FiniteAnimationSpec<E> = tween(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = JellyBounce
)

fun getAnimateEnterTransition(): EnterTransition =
    expandVertically(animationSpec = getAnimateTween()) +
        fadeIn(animationSpec = getAnimateTween())

fun getAnimateExitTransition(): ExitTransition =
    shrinkVertically(animationSpec = getAnimateTween()) +
        fadeOut(animationSpec = getAnimateTween())

/** 获取遵守全局动效设置的页面切换动画。 */
fun <E> getSwapAnimateTween(
    swapIn: Boolean,
    delayMillis: Int = 0
): FiniteAnimationSpec<E> {
    val adjustedDelayMillis = if (swapIn) getAdjustedDelayMillis(delayMillis) else 0
    return if (isSwapAnimateClosed()) {
        snap()
    } else {
        getAnimateTween(adjustedDelayMillis)
    }
}

/** 计算动画幅度。 */
fun getTargetValueByAmplitude(
    targetValue: Dp,
    amplitude: Int = 5
): Dp {
    val safeAmplitude = amplitude.coerceIn(0, 10)
    val minScale = 0.5f
    val maxScale = 1.5f
    val baseAmplitude = 5

    val scale = if (safeAmplitude == baseAmplitude) {
        1.0f
    } else if (safeAmplitude < baseAmplitude) {
        minScale + (safeAmplitude.toFloat() / baseAmplitude) * (1.0f - minScale)
    } else {
        1.0f + ((safeAmplitude - baseAmplitude).toFloat() / (10 - baseAmplitude)) * (maxScale - 1.0f)
    }

    return (targetValue.value * scale).dp
}

@Composable
fun swapAnimateDpAsState(
    targetValue: Dp,
    swapIn: Boolean,
    amplitude: Int = AllSettings.launcherAnimateExtent.state,
    isHorizontal: Boolean = false,
    delayMillis: Int = 0
): State<Dp> {
    return if (!isSwapAnimateClosed()) {
        swapAnimateDpAsState(
            targetValue = targetValue,
            swapIn = swapIn,
            amplitude = amplitude,
            isHorizontal = isHorizontal,
            animationSpec = getSwapAnimateTween(swapIn, delayMillis = delayMillis)
        )
    } else {
        rememberUpdatedState(newValue = 0.dp)
    }
}

@Composable
fun swapAnimateDpAsState(
    targetValue: Dp,
    swapIn: Boolean,
    amplitude: Int = AllSettings.launcherAnimateExtent.state,
    isHorizontal: Boolean = false,
    animationSpec: AnimationSpec<Dp>
): State<Dp> {
    val motionScale = AllSettings.launcherMotionLevel.state.staggerScale
    val value = if (swapIn || motionScale == 0f) {
        0.dp
    } else {
        getTargetValueByAmplitude(
            if (isHorizontal) targetValue / 2 else targetValue,
            amplitude
        ) * motionScale
    }
    return animateDpAsState(
        targetValue = value,
        animationSpec = animationSpec
    )
}

/** 计算根据倍速调整后的时间（毫秒）。 */
fun calculateAnimationTime(speed: Int, baseTime: Int, minFactor: Float = 0.25f): Int {
    val factor = 1f - (speed / 10f) * (1f - minFactor)
    return (baseTime * factor).toInt()
}
