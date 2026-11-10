/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.ui.screens.content.elements

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.utils.animation.getAnimateTween
import com.zhangyuhexht.zmcl.utils.animation.motionPressScale
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.utils.animation.swapAnimateDpAsState

@Composable
fun SectionNavigationMenu(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    selectedKey: TitledNavKey?,
    items: List<CategoryItem>,
    onNavigate: (TitledNavKey) -> Unit
) {
    val scrollState = rememberScrollState()
    val compact = LocalConfiguration.current.screenWidthDp < 720
    val railWidth = if (compact) 164.dp else 188.dp
    val itemHeight = if (compact) 48.dp else 52.dp
    val itemPadding = if (compact) 10.dp else 12.dp
    val xOffset = swapAnimateDpAsState(
        targetValue = (-28).dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    Surface(
        modifier = modifier
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
            .width(railWidth)
            .offset { IntOffset(x = xOffset.value.roundToPx(), y = 0) },
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { item ->
                if (item.division) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth(0.45f)
                            .align(Alignment.CenterHorizontally)
                            .alpha(0.35f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val selected = selectedKey == item.key
                val interactionSource = remember(item.key) { MutableInteractionSource() }
                val containerColor by animateColorAsState(
                    targetValue = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.01f)
                    },
                    animationSpec = getAnimateTween(),
                    label = "SectionItemContainer"
                )
                val itemContentColor by animateColorAsState(
                    targetValue = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = getAnimateTween(),
                    label = "SectionItemContent"
                )
                val indicatorColor by animateColorAsState(
                    targetValue = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                    },
                    animationSpec = getAnimateTween(),
                    label = "SectionItemIndicator"
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .motionPressScale(
                            interactionSource = interactionSource,
                            pressedScale = 0.975f,
                            label = "SectionItemScale"
                        ),
                    onClick = { onNavigate(item.key) },
                    interactionSource = interactionSource,
                    shape = RoundedCornerShape(16.dp),
                    color = containerColor,
                    contentColor = itemContentColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(horizontal = itemPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(24.dp)
                                .background(
                                    color = indicatorColor,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        item.icon()
                        Text(
                            modifier = Modifier.weight(1f),
                            text = androidx.compose.ui.res.stringResource(item.textRes),
                            maxLines = 1,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.SemiBold else null,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(2.dp))
        }
    }
}
