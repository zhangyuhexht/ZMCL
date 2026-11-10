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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.zhangyuhexht.zmcl.ui.screens.content.elements.DisabledAlpha
import com.zhangyuhexht.zmcl.utils.animation.getAnimateTween
import com.zhangyuhexht.zmcl.utils.animation.motionPressScale

/**
 * 导航栏item组件，可包含图标与文字。当item被选中时，
 * 会触发一个从中心向外扩展的胶囊形背景动画。
 *
 * @param text 用于定义item中文字内容的可组合函数
 * @param onClick 当该item被点击时调用的回调函数
 * @param selected 指示该item当前是否处于选中状态
 *                 选中动画的播放由此状态控制
 * @param icon 用于定义显示图标的可组合函数，图标显示在文字之前。
 * @param selectedPadding item被选中时，其内部内容的内边距
 * @param unSelectedPadding item未被选中时，其内部内容的内边距
 * @param shape 定义item裁剪边界与点击区域的形状。
 * @param backgroundColor item被选中时，显示的动画背景颜色。
 * @param selectedContentColor item被选中时，图标与文字的颜色。
 * @param unselectedContentColor item未被选中时，图标与文字的颜色。
 */
@Composable
fun TextRailItem(
    modifier: Modifier = Modifier,
    text: @Composable RowScope.() -> Unit,
    onClick: () -> Unit,
    icon: @Composable RowScope.() -> Unit = {},
    selected: Boolean,
    selectedPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    unSelectedPadding: PaddingValues = selectedPadding,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animationProgress = animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = getAnimateTween(),
        label = "SelectionAnimation"
    )

    Box(
        modifier = modifier
            .motionPressScale(
                interactionSource = interactionSource,
                enabled = enabled,
                label = "RailItemScale"
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                onClick = onClick
            )
            .alpha(if (enabled) 1f else DisabledAlpha)
    ) {
        //背景扩散动画
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val maxWidth = size.width
            val minWidth = 0f
            val currentWidth = minWidth + (maxWidth - minWidth) * animationProgress.value

            val left = (maxWidth - currentWidth) / 2

            //绘制胶囊形状背景
            drawRoundRect(
                color = backgroundColor,
                topLeft = Offset(left, 0f),
                size = Size(currentWidth, size.height),
                cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )
        }

        val paddingLeft = animateDpAsState(
            targetValue = if (selected) selectedPadding.calculateLeftPadding(LayoutDirection.Ltr) else unSelectedPadding.calculateLeftPadding(LayoutDirection.Ltr),
            animationSpec = getAnimateTween(),
            label = "RailPaddingStart"
        )
        val paddingRight = animateDpAsState(
            targetValue = if (selected) selectedPadding.calculateRightPadding(LayoutDirection.Ltr) else unSelectedPadding.calculateRightPadding(LayoutDirection.Ltr),
            animationSpec = getAnimateTween(),
            label = "RailPaddingEnd"
        )
        val paddingTop = animateDpAsState(
            targetValue = if (selected) selectedPadding.calculateTopPadding() else unSelectedPadding.calculateTopPadding(),
            animationSpec = getAnimateTween(),
            label = "RailPaddingTop"
        )
        val paddingBottom = animateDpAsState(
            targetValue = if (selected) selectedPadding.calculateBottomPadding() else unSelectedPadding.calculateBottomPadding(),
            animationSpec = getAnimateTween(),
            label = "RailPaddingBottom"
        )

        Row(
            modifier = Modifier.layout { measurable, constraints ->
                val start = paddingLeft.value.roundToPx()
                val end = paddingRight.value.roundToPx()
                val top = paddingTop.value.roundToPx()
                val bottom = paddingBottom.value.roundToPx()
                val horizontal = start + end
                val vertical = top + bottom
                val placeable = measurable.measure(
                    constraints.offset(horizontal = -horizontal, vertical = -vertical)
                )
                val width = (placeable.width + horizontal)
                    .coerceIn(constraints.minWidth, constraints.maxWidth)
                val height = (placeable.height + vertical)
                    .coerceIn(constraints.minHeight, constraints.maxHeight)

                layout(width, height) {
                    placeable.placeRelative(start, top)
                }
            },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contentColor by animateColorAsState(
                targetValue = if (selected) selectedContentColor else unselectedContentColor,
                animationSpec = getAnimateTween(),
                label = "RailContentColor"
            )
            CompositionLocalProvider(
                LocalContentColor provides contentColor
            ) {
                icon()
                text()
            }
        }
    }
}
