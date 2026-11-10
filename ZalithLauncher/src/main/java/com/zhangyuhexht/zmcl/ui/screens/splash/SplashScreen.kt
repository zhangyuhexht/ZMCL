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

package com.zhangyuhexht.zmcl.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zhangyuhexht.zmcl.BuildKeys
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.components.InstallableItem
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.setting.enums.AppLanguage
import com.zhangyuhexht.zmcl.setting.enums.LauncherIcon
import com.zhangyuhexht.zmcl.ui.base.FullscreenEffect
import com.zhangyuhexht.zmcl.ui.base.applyFullscreen
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.rememberTransitionSpec
import com.zhangyuhexht.zmcl.ui.theme.onBackgroundColor
import com.zhangyuhexht.zmcl.viewmodel.SplashBackStackViewModel

/**
 * @param startAllTask 开启全部的解压任务
 * @param unpackItems 解压任务列表
 */
@Composable
fun SplashScreen(
    startAllTask: () -> Unit,
    unpackItems: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel,
    onboardingCompleted: Boolean,
    setupStarted: Boolean,
    dependenciesReady: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    onSetupStarted: () -> Unit,
    onBackToLanguage: () -> Unit,
    onResourcesReady: () -> Unit,
    onSetupComplete: (SetupAccountChoice, String, LauncherIcon) -> Unit,
) {
    FullscreenEffect(AllSettings.launcherFullScreen.state)
    Column(
        modifier = Modifier.applyFullscreen(AllSettings.launcherFullScreen.state)
    ) {
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentColor = onBackgroundColor()
        )

        if (onboardingCompleted) {
            NavigationUI(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                startAllTask = startAllTask,
                unpackItems = unpackItems,
                screenViewModel = screenViewModel,
                dependenciesReady = dependenciesReady,
                onResourcesReady = onResourcesReady
            )
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                FirstRunScreen(
                    languageStep = !setupStarted,
                    preparing = setupStarted && !dependenciesReady,
                    dependenciesReady = dependenciesReady,
                    onLanguageSelected = onLanguageSelected,
                    onStartSetup = onSetupStarted,
                    onBackToLanguage = onBackToLanguage,
                    onSetupComplete = onSetupComplete
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    contentColor: Color,
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = BuildKeys.LAUNCHER_NAME
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.home_unofficial_notice),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    startAllTask: () -> Unit,
    unpackItems: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel,
    dependenciesReady: Boolean,
    onResourcesReady: () -> Unit
) {
    val backStack = screenViewModel.splashScreen.backStack

    if (backStack.isNotEmpty()) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(reverse = true),
            entryProvider = entryProvider {
                entry<NormalNavKey.UnpackDeps> {
                    UnpackScreen(
                        items = unpackItems,
                        screenViewModel = screenViewModel,
                        dependenciesReady = dependenciesReady,
                        onStart = startAllTask,
                        onContinue = onResourcesReady
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}
