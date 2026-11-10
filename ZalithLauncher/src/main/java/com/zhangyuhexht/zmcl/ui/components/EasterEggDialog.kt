/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.utils.animation.MotionLevel

private val EasterEggBlue = Color(0xFF4285F4)
private val EasterEggRed = Color(0xFFEA4335)
private val EasterEggYellow = Color(0xFFFBBC05)
private val EasterEggGreen = Color(0xFF34A853)

@Composable
fun ZmclEasterEggDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { AnimatedZmclMark() },
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            FilledTonalButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.generic_close))
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
private fun AnimatedZmclMark() {
    val motionLevel = AllSettings.launcherMotionLevel.state
    val reveal = remember { Animatable(if (motionLevel == MotionLevel.OFF) 1f else 0f) }

    LaunchedEffect(motionLevel) {
        if (motionLevel == MotionLevel.OFF) {
            reveal.snapTo(1f)
        } else {
            reveal.snapTo(0f)
            reveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (motionLevel.durationMillis * 2).coerceAtMost(480),
                    easing = EaseOutCubic
                )
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AnimatedMarkLetter("Z", EasterEggBlue, Color.White, 0) { reveal.value }
            AnimatedMarkLetter("M", EasterEggRed, Color.White, 1) { reveal.value }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AnimatedMarkLetter("C", EasterEggYellow, Color(0xFF202124), 2) { reveal.value }
            AnimatedMarkLetter("L", EasterEggGreen, Color.White, 3) { reveal.value }
        }
    }
}

@Composable
private fun AnimatedMarkLetter(
    letter: String,
    color: Color,
    contentColor: Color,
    index: Int,
    progress: () -> Float
) {
    Surface(
        modifier = Modifier
            .size(34.dp)
            .graphicsLayer {
                val itemProgress = ((progress() * 1.75f) - (index * 0.25f)).coerceIn(0f, 1f)
                alpha = itemProgress
                scaleX = 0.72f + (0.28f * itemProgress)
                scaleY = 0.72f + (0.28f * itemProgress)
                translationY = (1f - itemProgress) * 12.dp.toPx()
            },
        shape = MaterialTheme.shapes.medium,
        color = color,
        contentColor = contentColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
