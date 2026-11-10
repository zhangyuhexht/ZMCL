/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.setting.unit.EnumSettingUnit
import com.zhangyuhexht.zmcl.ui.components.IndicatorSlider
import com.zhangyuhexht.zmcl.ui.components.TitleAndSummary
import com.zhangyuhexht.zmcl.utils.animation.MotionLevel
import kotlin.math.roundToInt

private val motionSliderLevels = listOf(
    MotionLevel.OFF,
    MotionLevel.REDUCED,
    MotionLevel.STANDARD,
    MotionLevel.FULL
)

@Composable
fun MotionLevelSliderSettingsCard(
    unit: EnumSettingUnit<MotionLevel>,
    title: String,
    position: CardPosition,
    modifier: Modifier = Modifier,
    outerShape: Dp = 24.dp,
    innerShape: Dp = 6.dp
) {
    val selectedIndex = motionSliderLevels.indexOf(unit.state).coerceAtLeast(0)
    var sliderPosition by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
    val selectedLabel = stringResource(unit.state.textRes)

    LaunchedEffect(unit.state) {
        sliderPosition = motionSliderLevels.indexOf(unit.state).coerceAtLeast(0).toFloat()
    }

    SettingsCard(
        modifier = modifier,
        position = position,
        outerShape = outerShape,
        innerShape = innerShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TitleAndSummary(
                title = title,
                summary = stringResource(unit.state.summaryRes),
                titleStyle = MaterialTheme.typography.titleSmall,
                summaryStyle = MaterialTheme.typography.labelSmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IndicatorSlider(
                    modifier = Modifier
                        .weight(1f)
                        .semantics { stateDescription = selectedLabel },
                    value = sliderPosition,
                    onValueChange = { value ->
                        sliderPosition = value
                        val level = motionSliderLevels[value.roundToInt().coerceIn(0, 3)]
                        if (unit.state != level) unit.updateState(level)
                    },
                    onValueChangeFinished = {
                        val level = motionSliderLevels[sliderPosition.roundToInt().coerceIn(0, 3)]
                        unit.save(level)
                        sliderPosition = motionSliderLevels.indexOf(level).toFloat()
                    },
                    valueRange = 0f..3f,
                    steps = 2
                )
                Surface(
                    modifier = Modifier
                        .widthIn(min = 96.dp)
                        .padding(start = 12.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    AnimatedContent(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        targetState = unit.state,
                        transitionSpec = {
                            if (initialState == MotionLevel.OFF || targetState == MotionLevel.OFF) {
                                fadeIn(androidx.compose.animation.core.snap()) togetherWith
                                    fadeOut(androidx.compose.animation.core.snap())
                            } else {
                                fadeIn(tween(120, easing = EaseOutCubic)) togetherWith
                                    fadeOut(tween(70, easing = EaseOutCubic))
                            }
                        },
                        label = "motion-level-label"
                    ) { level ->
                        Text(
                            text = stringResource(level.textRes),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
