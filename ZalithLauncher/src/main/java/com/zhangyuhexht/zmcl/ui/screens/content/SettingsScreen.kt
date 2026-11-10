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

package com.zhangyuhexht.zmcl.ui.screens.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.elements.CategoryIcon
import com.zhangyuhexht.zmcl.ui.screens.content.elements.CategoryItem
import com.zhangyuhexht.zmcl.ui.screens.content.elements.SectionNavigationMenu
import com.zhangyuhexht.zmcl.ui.screens.content.settings.AboutInfoScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.ControlManageScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.ControlSettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.GameSettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.GamepadSettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.JavaManageScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.LauncherSettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.content.settings.RendererSettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.navigateOnce
import com.zhangyuhexht.zmcl.ui.screens.onBack
import com.zhangyuhexht.zmcl.ui.screens.rememberTransitionSpec
import com.zhangyuhexht.zmcl.viewmodel.ErrorViewModel
import com.zhangyuhexht.zmcl.viewmodel.EventViewModel
import com.zhangyuhexht.zmcl.viewmodel.ScreenBackStackViewModel

@Composable
fun SettingsScreen(
    key: NestedNavKey.Settings,
    backStackViewModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    BaseScreen(
        screenKey = key,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->

        Row(modifier = Modifier.fillMaxSize()) {
            TabMenu(
                modifier = Modifier.fillMaxHeight(),
                isVisible = isVisible,
                settingsScreenKey = backStackViewModel.settingsScreen.currentKey,
                navigateTo = { settingKey ->
                    key.backStack.navigateOnce(settingKey)
                }
            )
            NavigationUI(
                key = key,
                mainScreenKey = backStackViewModel.mainScreen.currentKey,
                settingsScreenKey = backStackViewModel.settingsScreen.currentKey,
                eventViewModel = eventViewModel,
                submitError = submitError,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

private val settingItems = listOf(
    CategoryItem(NormalNavKey.Settings.Renderer, { CategoryIcon(R.drawable.ic_video_settings, R.string.settings_tab_renderer) }, R.string.settings_tab_renderer),
    CategoryItem(NormalNavKey.Settings.Game, { CategoryIcon(R.drawable.ic_rocket_launch_filled, R.string.settings_tab_game) }, R.string.settings_tab_game),
    CategoryItem(NormalNavKey.Settings.Control, { CategoryIcon(R.drawable.ic_videogame_asset_outlined, R.string.settings_tab_control) }, R.string.settings_tab_control),
    CategoryItem(NormalNavKey.Settings.Gamepad, { CategoryIcon(R.drawable.ic_sports_esports_outlined, R.string.settings_tab_gamepad) }, R.string.settings_tab_gamepad),
    CategoryItem(NormalNavKey.Settings.Launcher, { CategoryIcon(R.drawable.ic_setting_launcher, R.string.settings_tab_launcher) }, R.string.settings_tab_launcher),
    CategoryItem(NormalNavKey.Settings.JavaManager, { CategoryIcon(R.drawable.ic_java, R.string.settings_tab_java_manage) }, R.string.settings_tab_java_manage, division = true),
    CategoryItem(NormalNavKey.Settings.ControlManager, { CategoryIcon(R.drawable.ic_videogame_asset_outlined, R.string.settings_tab_control_manage) }, R.string.settings_tab_control_manage),
    CategoryItem(NormalNavKey.Settings.AboutInfo, { CategoryIcon(R.drawable.ic_info_outlined, R.string.settings_tab_info_about) }, R.string.settings_tab_info_about, division = true)
)

@Composable
private fun TabMenu(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    settingsScreenKey: TitledNavKey?,
    navigateTo: (TitledNavKey) -> Unit
) {
    SectionNavigationMenu(
        modifier = modifier,
        isVisible = isVisible,
        selectedKey = settingsScreenKey,
        items = settingItems,
        onNavigate = navigateTo
    )
}

@Composable
private fun NavigationUI(
    key: NestedNavKey.Settings,
    mainScreenKey: TitledNavKey?,
    settingsScreenKey: TitledNavKey?,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStack = key.backStack

    if (backStack.isNotEmpty()) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(reverse = true),
            entryProvider = entryProvider {
                entry<NormalNavKey.Settings.Renderer> {
                    RendererSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel)
                }
                entry<NormalNavKey.Settings.Game> {
                    GameSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel)
                }
                entry<NormalNavKey.Settings.Control> {
                    ControlSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel, submitError)
                }
                entry<NormalNavKey.Settings.Gamepad> {
                    GamepadSettingsScreen(key, settingsScreenKey, mainScreenKey)
                }
                entry<NormalNavKey.Settings.Launcher> {
                    LauncherSettingsScreen(
                        key = key,
                        settingsScreenKey = settingsScreenKey,
                        mainScreenKey = mainScreenKey,
                    )
                }
                entry<NormalNavKey.Settings.JavaManager> {
                    JavaManageScreen(key, settingsScreenKey, mainScreenKey, submitError)
                }
                entry<NormalNavKey.Settings.ControlManager> {
                    ControlManageScreen(key, settingsScreenKey, mainScreenKey, eventViewModel, submitError)
                }
                entry<NormalNavKey.Settings.AboutInfo> {
                    AboutInfoScreen(
                        key = key,
                        settingsScreenKey = settingsScreenKey,
                        mainScreenKey = mainScreenKey,
                        openLink = { url ->
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(url))
                        }
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}
