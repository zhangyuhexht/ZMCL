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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.components.InstallableItem
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.viewmodel.SplashBackStackViewModel

@Composable
fun UnpackScreen(
    items: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel,
    dependenciesReady: Boolean,
    onStart: () -> Unit,
    onContinue: () -> Unit
) {
    BaseScreen(
        screenKey = NormalNavKey.UnpackDeps,
        currentKey = screenViewModel.splashScreen.currentKey
    ) {
        ResourcePreparationPage(
            items = items,
            dependenciesReady = dependenciesReady,
            onStart = onStart,
            onContinue = onContinue
        )
    }
}

@Composable
private fun ResourcePreparationPage(
    items: List<InstallableItem>,
    dependenciesReady: Boolean,
    onStart: () -> Unit,
    onContinue: () -> Unit
) {
    val states = items.map { item ->
        item.state.collectAsStateWithLifecycle().value
    }
    val completed = states.count { it == InstallableItem.State.FINISHED }
    val running = states.any { it == InstallableItem.State.RUNNING }
    val progress = if (items.isEmpty()) 1f else completed.toFloat() / items.size.toFloat()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        val columns = if (maxWidth >= 980.dp) 3 else 2
        val rows = items.chunked(columns)
        val compact = maxHeight < 430.dp
        val spacing = if (compact) 7.dp else 10.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.splash_screen_resources_title),
                        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (dependenciesReady) {
                            stringResource(R.string.splash_screen_resources_ready)
                        } else {
                            stringResource(R.string.splash_screen_unpack_desc)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (compact) 1 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = stringResource(
                        R.string.splash_screen_resources_progress,
                        completed,
                        items.size
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        rowItems.forEach { item ->
                            ResourceTaskItem(
                                item = item,
                                compact = compact,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                        repeat(columns - rowItems.size) {
                            Box(Modifier.weight(1f))
                        }
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 48.dp else 52.dp),
                enabled = dependenciesReady || !running,
                onClick = if (dependenciesReady) onContinue else onStart
            ) {
                if (running) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = when {
                        dependenciesReady -> stringResource(R.string.splash_screen_enter_launcher)
                        running -> stringResource(R.string.splash_screen_installing)
                        else -> stringResource(R.string.splash_screen_agree)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ResourceTaskItem(
    item: InstallableItem,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val state by item.state.collectAsStateWithLifecycle()
    val message by item.task.taskMessage.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 16.dp else 20.dp),
        color = when (state) {
            InstallableItem.State.FINISHED -> MaterialTheme.colorScheme.secondaryContainer
            InstallableItem.State.RUNNING -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        contentColor = when (state) {
            InstallableItem.State.FINISHED -> MaterialTheme.colorScheme.onSecondaryContainer
            InstallableItem.State.RUNNING -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 10.dp else 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
        ) {
            TaskStateIcon(state)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (state == InstallableItem.State.RUNNING) {
                        message ?: item.summary.orEmpty()
                    } else {
                        item.summary.orEmpty()
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TaskStateIcon(state: InstallableItem.State) {
    val modifier = Modifier.size(20.dp)
    when (state) {
        InstallableItem.State.RUNNING -> CircularProgressIndicator(
            modifier = modifier,
            strokeWidth = 2.dp
        )
        InstallableItem.State.FINISHED -> Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null
        )
        InstallableItem.State.PENDING -> Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_update),
            contentDescription = null
        )
        else -> Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_folder_zip_outlined),
            contentDescription = null
        )
    }
}
