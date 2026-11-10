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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.game.download.assets.platform.PlatformClasses
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.download.DownloadGameScreen
import com.zhangyuhexht.zmcl.ui.screens.content.download.DownloadModPackScreen
import com.zhangyuhexht.zmcl.ui.screens.content.download.DownloadModScreen
import com.zhangyuhexht.zmcl.ui.screens.content.download.DownloadResourcePackScreen
import com.zhangyuhexht.zmcl.ui.screens.content.download.DownloadSavesScreen
import com.zhangyuhexht.zmcl.ui.screens.content.download.DownloadShadersScreen
import com.zhangyuhexht.zmcl.ui.screens.content.download.assets.search.SearchIdScreen
import com.zhangyuhexht.zmcl.ui.screens.content.elements.CategoryIcon
import com.zhangyuhexht.zmcl.ui.screens.content.elements.CategoryItem
import com.zhangyuhexht.zmcl.ui.screens.content.elements.SectionNavigationMenu
import com.zhangyuhexht.zmcl.ui.screens.navigateOnce
import com.zhangyuhexht.zmcl.ui.screens.onBack
import com.zhangyuhexht.zmcl.ui.screens.rememberTransitionSpec
import com.zhangyuhexht.zmcl.viewmodel.ErrorViewModel
import com.zhangyuhexht.zmcl.viewmodel.EventViewModel
import com.zhangyuhexht.zmcl.viewmodel.ModpackImportViewModel
import com.zhangyuhexht.zmcl.viewmodel.ScreenBackStackViewModel

/**
 * 导航至DownloadScreen
 */
fun ScreenBackStackViewModel.navigateToDownload(targetScreen: TitledNavKey? = null) {
    downloadScreen.clearWith(targetScreen ?: downloadGameScreen)
    mainScreen.removeAndNavigateTo(
        removes = clearBeforeNavKeys,
        screenKey = downloadScreen,
        useClassEquality = true
    )
}

@Composable
fun DownloadScreen(
    key: NestedNavKey.Download,
    backScreenViewModel: ScreenBackStackViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    BaseScreen(
        screenKey = key,
        currentKey = backScreenViewModel.mainScreen.currentKey,
        useClassEquality = true
    ) { isVisible: Boolean ->
        Row(modifier = Modifier.fillMaxSize()) {
            TabMenu(
                modifier = Modifier.fillMaxHeight(),
                isVisible = isVisible,
                backStack = key.backStack,
                backScreenViewModel = backScreenViewModel,
            )

            NavigationUI(
                key = key,
                backScreenViewModel = backScreenViewModel,
                eventViewModel = eventViewModel,
                modpackImportViewModel = modpackImportViewModel,
                submitError = submitError,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun TabMenu(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    backStack: NavBackStack<TitledNavKey>,
    backScreenViewModel: ScreenBackStackViewModel,
) {
    val downloadsList = remember(backScreenViewModel) { listOf(
        CategoryItem(backScreenViewModel.downloadGameScreen, { CategoryIcon(R.drawable.ic_sports_esports_outlined, R.string.download_category_game) }, R.string.download_category_game),
        CategoryItem(backScreenViewModel.downloadModPackScreen, { CategoryIcon(R.drawable.ic_package_2_outlined, R.string.download_category_modpack) }, R.string.download_category_modpack),
        CategoryItem(backScreenViewModel.downloadModScreen, { CategoryIcon(R.drawable.ic_extension_outlined, R.string.download_category_mod) }, R.string.download_category_mod, division = true),
        CategoryItem(backScreenViewModel.downloadResourcePackScreen, { CategoryIcon(R.drawable.ic_format_paint_outlined, R.string.download_category_resource_pack) }, R.string.download_category_resource_pack),
        CategoryItem(backScreenViewModel.downloadSavesScreen, { CategoryIcon(R.drawable.ic_public, R.string.download_category_saves) }, R.string.download_category_saves),
        CategoryItem(backScreenViewModel.downloadShadersScreen, { CategoryIcon(R.drawable.ic_lightbulb, R.string.download_category_shaders) }, R.string.download_category_shaders),
        CategoryItem(NormalNavKey.SearchId, { CategoryIcon(R.drawable.ic_card, R.string.download_category_by_id) }, R.string.download_category_by_id, division = true),
    ) }

    SectionNavigationMenu(
        modifier = modifier,
        isVisible = isVisible,
        selectedKey = backScreenViewModel.downloadScreen.currentKey,
        items = downloadsList,
        onNavigate = { backStack.navigateOnce(it) }
    )
}

@Composable
private fun NavigationUI(
    key: NestedNavKey.Download,
    backScreenViewModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
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
                entry<NestedNavKey.DownloadGame> { key ->
                    DownloadGameScreen(
                        key = key,
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        downloadGameScreenKey = backScreenViewModel.downloadGameScreen.currentKey,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NestedNavKey.DownloadModPack> { key ->
                    DownloadModPackScreen(
                        key = key,
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        downloadModPackScreenKey = backScreenViewModel.downloadModPackScreen.currentKey,
                        eventViewModel = eventViewModel,
                        importerViewModel = modpackImportViewModel
                    )
                }
                entry<NestedNavKey.DownloadMod> { key ->
                    DownloadModScreen(
                        key = key,
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        downloadModScreenKey = backScreenViewModel.downloadModScreen.currentKey,
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NestedNavKey.DownloadResourcePack> { key ->
                    DownloadResourcePackScreen(
                        key = key,
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        downloadResourcePackScreenKey = backScreenViewModel.downloadResourcePackScreen.currentKey,
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NestedNavKey.DownloadSaves> { key ->
                    DownloadSavesScreen(
                        key = key,
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        downloadSavesScreenKey = backScreenViewModel.downloadSavesScreen.currentKey,
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NestedNavKey.DownloadShaders> { key ->
                    DownloadShadersScreen(
                        key = key,
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        downloadShadersScreenKey = backScreenViewModel.downloadShadersScreen.currentKey,
                        submitError = submitError,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NormalNavKey.SearchId> {
                    SearchIdScreen(
                        mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                        downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                        swapToDownload = { platform, classes, projectId, iconUrl ->
                            val backStack = when (classes) {
                                PlatformClasses.MOD -> backScreenViewModel.downloadModScreen
                                PlatformClasses.MOD_PACK -> backScreenViewModel.downloadModPackScreen
                                PlatformClasses.RESOURCE_PACK -> backScreenViewModel.downloadResourcePackScreen
                                PlatformClasses.SAVES -> backScreenViewModel.downloadSavesScreen
                                PlatformClasses.SHADERS -> backScreenViewModel.downloadShadersScreen
                            }
                            backScreenViewModel.navigateToDownload(
                                targetScreen = backStack.apply {
                                    navigateTo(
                                        NormalNavKey.DownloadAssets(
                                            platform = platform,
                                            projectId = projectId,
                                            classes = PlatformClasses.MOD,
                                            iconUrl = iconUrl
                                        )
                                    )
                                }
                            )
                        },
                        openLink = { link ->
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(link))
                        }
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}
