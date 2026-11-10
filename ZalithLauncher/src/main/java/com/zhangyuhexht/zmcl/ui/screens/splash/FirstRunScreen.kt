/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.ui.screens.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.setting.enums.AppLanguage
import com.zhangyuhexht.zmcl.setting.enums.LauncherIcon
import com.zhangyuhexht.zmcl.ui.components.IndicatorSlider
import com.zhangyuhexht.zmcl.ui.theme.AccentColor
import com.zhangyuhexht.zmcl.utils.animation.MotionLevel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SetupAccountChoice {
    OFFLINE,
    MICROSOFT,
    EXTERNAL,
    SKIP
}

private val onboardingMotionLevels = listOf(
    MotionLevel.OFF,
    MotionLevel.REDUCED,
    MotionLevel.STANDARD,
    MotionLevel.FULL
)

@Composable
fun FirstRunScreen(
    languageStep: Boolean,
    preparing: Boolean,
    dependenciesReady: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    onStartSetup: () -> Unit,
    onBackToLanguage: () -> Unit,
    onSetupComplete: (SetupAccountChoice, String, LauncherIcon) -> Unit,
) {
    var language by remember { mutableStateOf(AllSettings.launcherLanguage.state) }
    var setupPage by remember { mutableIntStateOf(0) }
    var accent by remember { mutableStateOf(AllSettings.launcherAccentColor.state) }
    var motionLevel by remember { mutableStateOf(AllSettings.launcherMotionLevel.state) }
    var launcherIcon by remember { mutableStateOf(AllSettings.launcherIcon.state) }
    var accountChoice by remember { mutableStateOf(SetupAccountChoice.SKIP) }
    var username by remember { mutableStateOf("ZMCL_Player") }
    val coroutineScope = rememberCoroutineScope()
    var setupActionPending by remember { mutableStateOf(false) }

    fun deferSetupAction(action: () -> Unit) {
        if (setupActionPending) return
        setupActionPending = true
        coroutineScope.launch {
            try {
                // Let the pressed state and ripple draw before creating the next
                // step, which may contain a large image grid.
                withFrameNanos { }
                action()
            } finally {
                setupActionPending = false
            }
        }
    }

    val currentStepIndex = if (languageStep) 1 else setupPage + 2
    val (headerTitle, headerSummary) = when {
        languageStep -> Pair(
            stringResource(R.string.splash_screen_language_title),
            stringResource(R.string.splash_screen_language_summary)
        )
        setupPage == 0 -> Pair(
            stringResource(R.string.splash_screen_setup_title),
            stringResource(R.string.splash_screen_setup_summary)
        )
        setupPage == 1 -> Pair(
            stringResource(R.string.splash_screen_motion_title),
            stringResource(R.string.splash_screen_motion_summary)
        )
        setupPage == 2 -> Pair(
            stringResource(R.string.splash_screen_icon_title),
            stringResource(R.string.splash_screen_icon_summary)
        )
        else -> Pair(
            stringResource(R.string.splash_screen_setup_account),
            stringResource(R.string.splash_screen_setup_account_summary)
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val compact = maxHeight < 460.dp

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                FixedStepHeader(
                    title = headerTitle,
                    summary = headerSummary,
                    stepIndex = currentStepIndex,
                    compact = compact,
                    motionLevel = motionLevel
                )

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AnimatedContent(
                        targetState = languageStep,
                        transitionSpec = {
                            when (motionLevel) {
                                MotionLevel.OFF -> fadeIn(snap()) togetherWith fadeOut(snap())
                                MotionLevel.REDUCED -> fadeIn(tween(motionLevel.durationMillis, easing = EaseOutCubic)) togetherWith
                                    fadeOut(tween(motionLevel.durationMillis, easing = EaseOutCubic))
                                else -> fadeIn(
                                    tween(motionLevel.durationMillis, easing = EaseOutCubic)
                                ) togetherWith fadeOut(
                                    tween(80, easing = EaseOutCubic)
                                )
                            }
                        },
                        label = "first-run-step"
                    ) { showLanguage ->
                        if (showLanguage) {
                            LanguageStep(
                                selected = language,
                                compact = compact,
                                onSelected = {
                                    language = it
                                    onLanguageSelected(it)
                                },
                                onStart = { deferSetupAction(onStartSetup) }
                            )
                        } else {
                            AnimatedContent(
                                targetState = setupPage,
                                transitionSpec = {
                                    when (motionLevel) {
                                        MotionLevel.OFF -> fadeIn(snap()) togetherWith fadeOut(snap())
                                        MotionLevel.REDUCED -> fadeIn(tween(motionLevel.durationMillis, easing = EaseOutCubic)) togetherWith
                                            fadeOut(tween(motionLevel.durationMillis, easing = EaseOutCubic))
                                        else -> fadeIn(
                                            tween(motionLevel.durationMillis, easing = EaseOutCubic)
                                        ) togetherWith fadeOut(
                                            tween(80, easing = EaseOutCubic)
                                        )
                                    }
                                },
                                label = "first-run-setup-page"
                            ) { page ->
                                when (page) {
                                    0 -> ThemeStep(
                                        selected = accent,
                                        preparing = preparing,
                                        compact = compact,
                                        onSelected = {
                                            accent = it
                                            // Update the in-memory theme immediately, but defer
                                            // disk persistence until the step is confirmed.
                                            AllSettings.launcherAccentColor.updateState(it)
                                        },
                                        onContinue = {
                                            deferSetupAction {
                                                AllSettings.launcherAccentColor.save(accent)
                                                setupPage = 1
                                            }
                                        },
                                        onBackToLanguage = {
                                            deferSetupAction {
                                                setupPage = 0
                                                onBackToLanguage()
                                            }
                                        }
                                    )
                                    1 -> MotionStep(
                                        selected = motionLevel,
                                        compact = compact,
                                        onSelected = {
                                            motionLevel = it
                                            AllSettings.launcherMotionLevel.updateState(it)
                                        },
                                        onBack = { deferSetupAction { setupPage = 0 } },
                                        onContinue = {
                                            deferSetupAction {
                                                AllSettings.launcherMotionLevel.save(motionLevel)
                                                setupPage = 2
                                            }
                                        }
                                    )
                                    2 -> IconStep(
                                        selected = launcherIcon,
                                        preparing = preparing,
                                        compact = compact,
                                        onSelected = {
                                            launcherIcon = it
                                        },
                                        onBack = { deferSetupAction { setupPage = 1 } },
                                        onContinue = { deferSetupAction { setupPage = 3 } }
                                    )
                                    else -> AccountStep(
                                        choice = accountChoice,
                                        username = username,
                                        dependenciesReady = dependenciesReady,
                                        compact = compact,
                                        onChoice = { accountChoice = it },
                                        onUsernameChange = { username = it },
                                        onBack = { deferSetupAction { setupPage = 2 } },
                                        onContinue = {
                                            deferSetupAction {
                                                onSetupComplete(accountChoice, username, launcherIcon)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageStep(
    selected: AppLanguage,
    compact: Boolean = false,
    onSelected: (AppLanguage) -> Unit,
    onStart: () -> Unit
) {
    val languageRows = remember { AppLanguage.entries.chunked(2) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (compact) 16.dp else 28.dp, vertical = if (compact) 8.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp, Alignment.CenterVertically)
        ) {
            languageRows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp)
                ) {
                    rowItems.forEach { item ->
                        ChoiceCard(
                            modifier = Modifier.weight(1f),
                            selected = item == selected,
                            onClick = { onSelected(item) },
                            title = stringResource(item.textRes),
                            compact = compact
                        )
                    }
                    repeat(2 - rowItems.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 44.dp else 52.dp),
            onClick = onStart
        ) {
            Text(stringResource(R.string.splash_screen_start))
        }
    }
}

@Composable
private fun MotionStep(
    selected: MotionLevel,
    compact: Boolean = false,
    onSelected: (MotionLevel) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val edgePadding = if (compact) 16.dp else 28.dp
    val spacing = if (compact) 8.dp else 12.dp
    val selectedIndex = onboardingMotionLevels.indexOf(selected).coerceAtLeast(0)
    var sliderPosition by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
    val selectedLabel = stringResource(selected.textRes)

    LaunchedEffect(selected) {
        sliderPosition = onboardingMotionLevels.indexOf(selected).coerceAtLeast(0).toFloat()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = edgePadding, vertical = if (compact) 8.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (compact) 14.dp else 24.dp, vertical = if (compact) 10.dp else 18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = selected,
                    transitionSpec = {
                        if (initialState == MotionLevel.OFF || targetState == MotionLevel.OFF) {
                            fadeIn(snap()) togetherWith fadeOut(snap())
                        } else {
                            fadeIn(tween(120, easing = EaseOutCubic)) togetherWith
                                fadeOut(tween(70, easing = EaseOutCubic))
                        }
                    },
                    label = "onboarding-motion-level"
                ) { level ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(level.textRes),
                            style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(level.summaryRes),
                            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IndicatorSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (compact) 8.dp else 16.dp)
                        .semantics { stateDescription = selectedLabel },
                    value = sliderPosition,
                    onValueChange = { value ->
                        sliderPosition = value
                        val level = onboardingMotionLevels[value.roundToInt().coerceIn(0, 3)]
                        if (selected != level) onSelected(level)
                    },
                    onValueChangeFinished = {
                        sliderPosition = onboardingMotionLevels.indexOf(selected).coerceAtLeast(0).toFloat()
                    },
                    valueRange = 0f..3f,
                    steps = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    onboardingMotionLevels.forEachIndexed { index, level ->
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.shapes.small)
                                .clickable {
                                    sliderPosition = index.toFloat()
                                    if (selected != level) onSelected(level)
                                }
                                .padding(vertical = 6.dp),
                            text = stringResource(level.textRes),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (level == selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (level == selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onBack
            ) {
                Text(stringResource(R.string.generic_back), maxLines = 1)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onContinue
            ) {
                Text(stringResource(R.string.splash_screen_setup_continue), maxLines = 1)
            }
        }
    }
}

@Composable
private fun IconStep(
    selected: LauncherIcon,
    preparing: Boolean,
    compact: Boolean = false,
    onSelected: (LauncherIcon) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val iconRows = remember { LauncherIcon.entries.chunked(4) }
    val edgePadding = if (compact) 16.dp else 28.dp
    val verticalPadding = if (compact) 8.dp else 12.dp
    val spacing = if (compact) 8.dp else 14.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = edgePadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
        ) {
            iconRows.forEach { icons ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
                ) {
                    icons.forEach { icon ->
                        IconChoiceCard(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            icon = icon,
                            selected = icon == selected,
                            compact = compact,
                            onClick = { onSelected(icon) }
                        )
                    }
                    repeat(4 - icons.size) {
                        Box(Modifier.weight(1f))
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onBack
            ) {
                Text(stringResource(R.string.generic_back), maxLines = 1)
            }
            if (preparing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (compact) 18.dp else 22.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    modifier = Modifier.weight(1.4f),
                    text = stringResource(R.string.splash_screen_installing),
                    style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Box(Modifier.weight(1.4f))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onContinue
            ) {
                Text(stringResource(R.string.splash_screen_setup_continue), maxLines = 1)
            }
        }
    }
}

@Composable
private fun IconChoiceCard(
    icon: LauncherIcon,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val press = rememberPressScale()
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = press.scale.value
                scaleY = press.scale.value
            }
            .clickable(
                interactionSource = press.interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp)
        ) {
            LauncherIconPreview(
                icon = icon,
                modifier = Modifier.size(if (compact) 54.dp else 72.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(icon.labelRes),
                    style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                RadioButton(selected = selected, onClick = onClick)
            }
        }
    }
}

@Composable
private fun LauncherIconPreview(
    icon: LauncherIcon,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clip(MaterialTheme.shapes.large),
        contentAlignment = Alignment.Center
    ) {
        icon.previewBackgroundRes?.let { backgroundRes ->
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(icon.previewForegroundRes),
            contentDescription = null
        )
    }
}

@Composable
private fun ThemeStep(
    selected: AccentColor,
    preparing: Boolean,
    compact: Boolean = false,
    onSelected: (AccentColor) -> Unit,
    onContinue: () -> Unit,
    onBackToLanguage: () -> Unit
) {
    val gridColumns = 4
    val edgePadding = if (compact) 16.dp else 28.dp
    val verticalPadding = if (compact) 8.dp else 12.dp
    val sectionSpacing = if (compact) 8.dp else 14.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = edgePadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(sectionSpacing)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier.padding(if (compact) 10.dp else 18.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.splash_screen_setup_theme),
                    style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium
                )
                AccentGrid(
                    selected = selected,
                    compact = compact,
                    columns = gridColumns,
                    onSelected = onSelected
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onBackToLanguage
            ) {
                Text(
                    text = stringResource(R.string.generic_back),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (preparing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (compact) 18.dp else 22.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = stringResource(R.string.splash_screen_installing),
                    modifier = Modifier.weight(1.4f),
                    maxLines = if (compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall
                )
            } else {
                Box(Modifier.weight(1.4f))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onContinue
            ) {
                Text(
                    text = stringResource(R.string.splash_screen_setup_continue),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AccentGrid(
    selected: AccentColor,
    compact: Boolean,
    columns: Int,
    onSelected: (AccentColor) -> Unit
) {
    val accentRows = remember(columns) { AccentColor.entries.chunked(columns) }
    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 12.dp)) {
        accentRows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp)
            ) {
                rowItems.forEach { item ->
                    AccentSwatch(
                        modifier = Modifier.weight(1f),
                        item = item,
                        selected = item == selected,
                        compact = compact,
                        onClick = { onSelected(item) }
                    )
                }
                repeat(columns - rowItems.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AccentSwatch(
    item: AccentColor,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val press = rememberPressScale()
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .graphicsLayer {
                scaleX = press.scale.value
                scaleY = press.scale.value
            }
            .clickable(
                interactionSource = press.interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(vertical = if (compact) 2.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            modifier = Modifier.size(if (compact) 28.dp else 38.dp),
            shape = CircleShape,
            color = item.primary,
            border = if (selected) androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface) else null
        ) {
            if (selected) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = item.onPrimary,
                        modifier = Modifier.size(if (compact) 16.dp else 20.dp)
                    )
                }
            }
        }
        Text(
            text = stringResource(item.labelRes),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AccountStep(
    choice: SetupAccountChoice,
    username: String,
    dependenciesReady: Boolean,
    compact: Boolean = false,
    onChoice: (SetupAccountChoice) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (compact) 16.dp else 28.dp, vertical = if (compact) 8.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 12.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccountChoiceCard(
                modifier = Modifier.weight(1f),
                selected = choice == SetupAccountChoice.OFFLINE,
                onClick = { onChoice(SetupAccountChoice.OFFLINE) },
                title = stringResource(R.string.splash_screen_setup_offline),
                compact = compact
            )
            AccountChoiceCard(
                modifier = Modifier.weight(1f),
                selected = choice == SetupAccountChoice.MICROSOFT,
                onClick = { onChoice(SetupAccountChoice.MICROSOFT) },
                title = stringResource(R.string.splash_screen_setup_microsoft),
                compact = compact
            )
            AccountChoiceCard(
                modifier = Modifier.weight(1f),
                selected = choice == SetupAccountChoice.EXTERNAL,
                onClick = { onChoice(SetupAccountChoice.EXTERNAL) },
                title = stringResource(R.string.splash_screen_setup_external),
                compact = compact
            )
        }
        if (choice == SetupAccountChoice.OFFLINE) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = onUsernameChange,
                singleLine = true,
                label = { Text(stringResource(R.string.splash_screen_setup_username)) },
                placeholder = { Text(stringResource(R.string.splash_screen_setup_username_hint)) }
            )
        } else if (choice != SetupAccountChoice.SKIP) {
            Text(
                text = stringResource(R.string.splash_screen_setup_login_later),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onBack
            ) {
                Text(stringResource(R.string.generic_back), maxLines = 1)
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { onChoice(SetupAccountChoice.SKIP) }
            ) {
                Text(stringResource(R.string.splash_screen_setup_skip), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onContinue
            ) {
                Text(
                    if (dependenciesReady) {
                        stringResource(R.string.splash_screen_enter_launcher)
                    } else {
                        stringResource(R.string.splash_screen_setup_continue)
                    }
                )
            }
        }
    }
}

@Composable
private fun AccountChoiceCard(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val press = rememberPressScale()
    Surface(
        modifier = modifier
            .height(if (compact) 76.dp else 92.dp)
            .graphicsLayer {
                scaleX = press.scale.value
                scaleY = press.scale.value
            }
            .clickable(
                interactionSource = press.interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 8.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Text(title, style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ChoiceCard(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val press = rememberPressScale()
    Surface(
        modifier = modifier
            .height(if (compact) 60.dp else 74.dp)
            .graphicsLayer {
                scaleX = press.scale.value
                scaleY = press.scale.value
            }
            .clickable(
                interactionSource = press.interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp)
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Text(title, style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

private data class PressScaleState(
    val interactionSource: MutableInteractionSource,
    val scale: State<Float>
)

@Composable
private fun rememberPressScale(): PressScaleState {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val motionLevel = AllSettings.launcherMotionLevel.state
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = when (motionLevel) {
            MotionLevel.OFF -> snap()
            MotionLevel.FULL -> tween(112, easing = EaseOutCubic)
            MotionLevel.STANDARD -> tween(96, easing = EaseOutCubic)
            MotionLevel.REDUCED -> tween(72, easing = EaseOutCubic)
        },
        label = "setup-press-scale"
    )
    return PressScaleState(interactionSource, scale)
}

@Composable
private fun FixedStepHeader(
    title: String,
    summary: String,
    compact: Boolean = false,
    stepIndex: Int,
    motionLevel: MotionLevel,
    totalSteps: Int = 5
) {
    val safeStep = stepIndex.coerceIn(0, totalSteps)
    val targetProgress = if (totalSteps <= 1) {
        0f
    } else {
        (safeStep - 1).coerceAtLeast(0).toFloat() / (totalSteps - 1).toFloat()
    }
    val progress = animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = when (motionLevel) {
            MotionLevel.OFF -> snap()
            MotionLevel.FULL -> tween(280, easing = EaseOutCubic)
            MotionLevel.STANDARD -> tween(220, easing = EaseOutCubic)
            MotionLevel.REDUCED -> tween(150, easing = EaseOutCubic)
        },
        label = "setup-step-progress"
    )
    val pulseAlpha = if (motionLevel == MotionLevel.OFF || motionLevel == MotionLevel.REDUCED) {
        null
    } else {
        val pulseTransition = rememberInfiniteTransition(label = "setup-progress-pulse")
        pulseTransition.animateFloat(
            initialValue = 0.28f,
            targetValue = 0.72f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (motionLevel == MotionLevel.FULL) 760 else 960,
                    easing = EaseOutCubic
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "setup-progress-pulse-alpha"
        )
    }

    Column(
        modifier = Modifier.padding(horizontal = if (compact) 16.dp else 28.dp, vertical = if (compact) 6.dp else 10.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (compact) 20.dp else 24.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .height(if (compact) 4.dp else 5.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalSteps) { index ->
                        val completed = index < safeStep - 1
                        val isCurrent = index == safeStep - 1
                        val dotColor by animateColorAsState(
                            targetValue = if (completed || isCurrent) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            },
                            animationSpec = if (motionLevel == MotionLevel.OFF) {
                                snap()
                            } else {
                                tween(motionLevel.durationMillis, easing = EaseOutCubic)
                            },
                            label = "setup-progress-node"
                        )
                        Box(
                            modifier = Modifier.size(if (compact) 20.dp else 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent && pulseAlpha != null) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = pulseAlpha.value * 0.32f },
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {}
                            }
                            Surface(
                                modifier = Modifier.size(if (compact) 14.dp else 16.dp),
                                shape = CircleShape,
                                color = dotColor,
                                contentColor = if (completed || isCurrent) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                border = if (completed || isCurrent) {
                                    null
                                } else {
                                    androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (completed) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_check),
                                            contentDescription = null,
                                            modifier = Modifier.size(if (compact) 9.dp else 10.dp)
                                        )
                                    } else {
                                        Text(
                                            text = (index + 1).toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    text = stringResource(R.string.splash_screen_step_progress, safeStep, totalSteps),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
        Text(
            title,
            style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            summary,
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            maxLines = if (compact) 2 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
    }
}
