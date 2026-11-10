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

package com.zhangyuhexht.zmcl.ui.screens.main

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.coroutine.Task
import com.zhangyuhexht.zmcl.coroutine.TaskSystem
import com.zhangyuhexht.zmcl.game.version.installed.Version
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.ui.base.FullscreenEffect
import com.zhangyuhexht.zmcl.ui.base.applyFullscreen
import com.zhangyuhexht.zmcl.ui.components.BackgroundCard
import com.zhangyuhexht.zmcl.ui.components.CardTitleLayout
import com.zhangyuhexht.zmcl.ui.components.ZmclEasterEggDialog
import com.zhangyuhexht.zmcl.ui.screens.BackStackNavKey
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.AccountManageScreen
import com.zhangyuhexht.zmcl.ui.screens.content.DownloadScreen
import com.zhangyuhexht.zmcl.ui.screens.content.FileSelectorScreen
import com.zhangyuhexht.zmcl.ui.screens.content.HomePageEditorScreen
import com.zhangyuhexht.zmcl.ui.screens.content.LauncherScreen
import com.zhangyuhexht.zmcl.ui.screens.content.LicenseScreen
import com.zhangyuhexht.zmcl.ui.screens.content.LogViewScreen
import com.zhangyuhexht.zmcl.ui.screens.content.MultiplayerScreen
import com.zhangyuhexht.zmcl.ui.screens.content.SettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.content.VersionExportScreen
import com.zhangyuhexht.zmcl.ui.screens.content.VersionSettingsScreen
import com.zhangyuhexht.zmcl.ui.screens.content.VersionsManageScreen
import com.zhangyuhexht.zmcl.ui.screens.content.WebViewScreen
import com.zhangyuhexht.zmcl.ui.screens.content.navigateToDownload
import com.zhangyuhexht.zmcl.ui.screens.navigateTo
import com.zhangyuhexht.zmcl.ui.screens.onBack
import com.zhangyuhexht.zmcl.ui.screens.rememberTransitionSpec
import com.zhangyuhexht.zmcl.ui.theme.backgroundColor
import com.zhangyuhexht.zmcl.ui.theme.cardColor
import com.zhangyuhexht.zmcl.ui.theme.onBackgroundColor
import com.zhangyuhexht.zmcl.ui.theme.onCardColor
import com.zhangyuhexht.zmcl.utils.animation.getAnimateTween
import com.zhangyuhexht.zmcl.utils.animation.motionPressScale
import com.zhangyuhexht.zmcl.utils.file.formatFileSize
import com.zhangyuhexht.zmcl.viewmodel.ErrorViewModel
import com.zhangyuhexht.zmcl.viewmodel.EventViewModel
import com.zhangyuhexht.zmcl.viewmodel.LocalBackgroundViewModel
import com.zhangyuhexht.zmcl.viewmodel.ModpackImportViewModel
import com.zhangyuhexht.zmcl.viewmodel.ScreenBackStackViewModel
import com.zhangyuhexht.zmcl.viewmodel.sendKeepScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BrandBlue = Color(0xFF4285F4)
private val BrandRed = Color(0xFFEA4335)
private val BrandYellow = Color(0xFFFBBC05)
private val BrandGreen = Color(0xFF34A853)

@Composable
fun MainScreen(
    screenBackStackModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()

    //监控当前是否有任务正在进行
    LaunchedEffect(tasks) {
        if (tasks.isEmpty()) {
            eventViewModel.sendKeepScreen(false)
        } else {
            //有任务正在进行，避免熄屏
            eventViewModel.sendKeepScreen(true)
        }
    }

    val isTaskMenuExpanded = AllSettings.launcherTaskMenuExpanded.state

    fun changeTasksExpandedState() {
        AllSettings.launcherTaskMenuExpanded.save(!isTaskMenuExpanded)
    }

    /** 回到主页面通用函数 */
    val toMainScreen: () -> Unit = {
        screenBackStackModel.mainScreen.clearWith(NormalNavKey.LauncherMain)
    }

    val mainScreenKey = screenBackStackModel.mainScreen.currentKey
    val inLauncherScreen = mainScreenKey == null || mainScreenKey is NormalNavKey.LauncherMain

    val isBackgroundValid = LocalBackgroundViewModel.current?.isValid == true
    val launcherBackgroundOpacity = AllSettings.launcherBackgroundOpacity.state.toFloat() / 100f

    val backgroundColor = if (isBackgroundValid) {
        backgroundColor().copy(alpha = launcherBackgroundOpacity)
    } else backgroundColor()

    FullscreenEffect(AllSettings.launcherFullScreen.state)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
        contentColor = onBackgroundColor()
    ) {
        Column(
            modifier = Modifier
                .applyFullscreen(AllSettings.launcherFullScreen.state)
        ) {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                mainScreenKey = mainScreenKey,
                inLauncherScreen = inLauncherScreen,
                taskRunning = tasks.isEmpty(),
                isTasksExpanded = isTaskMenuExpanded,
                contentColor = onBackgroundColor(),
                onScreenBack = {
                    screenBackStackModel.mainScreen.backStack.removeFirstOrNull()
                },
                toMainScreen = toMainScreen,
                toSettingsScreen = {
                    screenBackStackModel.mainScreen.removeAndNavigateTo(
                        removes = screenBackStackModel.clearBeforeNavKeys,
                        screenKey = screenBackStackModel.settingsScreen
                    )
                },
                toDownloadScreen = {
                    screenBackStackModel.navigateToDownload()
                },
                changeExpandedState = {
                    changeTasksExpandedState()
                },
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                NavigationUI(
                    modifier = Modifier.fillMaxSize(),
                    screenBackStackModel = screenBackStackModel,
                    toMainScreen = toMainScreen,
                    eventViewModel = eventViewModel,
                    modpackImportViewModel = modpackImportViewModel,
                    submitError = submitError
                )

                TaskMenu(
                    tasks = tasks,
                    isExpanded = isTaskMenuExpanded,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .align(Alignment.CenterStart)
                        .padding(all = 6.dp)
                ) {
                    changeTasksExpandedState()
                }
            }
        }
    }
}

@Composable
private fun <E: TitledNavKey> TopBar(
    mainScreenKey: E?,
    inLauncherScreen: Boolean,
    taskRunning: Boolean,
    isTasksExpanded: Boolean,
    modifier: Modifier = Modifier,
    contentColor: Color,
    onScreenBack: () -> Unit,
    toMainScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    toDownloadScreen: () -> Unit,
    changeExpandedState: () -> Unit,
) {
    val inDownloadScreen = mainScreenKey is NestedNavKey.Download
    val inSettingsScreen = mainScreenKey is NestedNavKey.Settings
    val hapticFeedback = LocalHapticFeedback.current
    var brandTapCount by remember { mutableIntStateOf(0) }
    var showBrandEasterEgg by remember { mutableStateOf(false) }

    LaunchedEffect(brandTapCount) {
        if (brandTapCount > 0) {
            val expectedTapCount = brandTapCount
            delay(1600L)
            if (brandTapCount == expectedTapCount) brandTapCount = 0
        }
    }

    if (showBrandEasterEgg) {
        ZmclEasterEggDialog(
            title = stringResource(R.string.easter_egg_brand_title),
            message = stringResource(R.string.easter_egg_brand_message),
            onDismiss = { showBrandEasterEgg = false }
        )
    }

    CompositionLocalProvider(
        LocalContentColor provides contentColor
    ) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 1.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
            )
        ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            val (backCenter, title, endButtons) = createRefs()

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            Row(
                modifier = Modifier
                    .constrainAs(backCenter) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxHeight()
            ) {
                AnimatedVisibility(
                    visible = !inLauncherScreen,
                    enter = fadeIn(animationSpec = getAnimateTween()),
                    exit = fadeOut(animationSpec = getAnimateTween())
                ) {
                    Row(modifier = Modifier.fillMaxHeight()) {
                        Spacer(Modifier.width(12.dp))

                        IconButton(
                            modifier = Modifier.fillMaxHeight(),
                            onClick = {
                                if (!inLauncherScreen) {
                                    //不在主屏幕时才允许返回
                                    backDispatcher?.onBackPressed() ?: run {
                                        onScreenBack()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.generic_back)
                            )
                        }

                        IconButton(
                            modifier = Modifier.fillMaxHeight(),
                            onClick = {
                                if (!inLauncherScreen) {
                                    //不在主屏幕时才允许回到主页面
                                    toMainScreen()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_home_filled),
                                contentDescription = stringResource(R.string.generic_main_menu)
                            )
                        }
                    }
                }
            }
            val parentRes = mainScreenKey?.title
            val childRes = (mainScreenKey as? BackStackNavKey<*>)?.currentKey?.title

            Crossfade(
                modifier = Modifier.constrainAs(title) {
                    centerVerticallyTo(parent)
                    start.linkTo(backCenter.end, margin = 16.dp)
                    end.linkTo(endButtons.start, margin = 16.dp)
                    width = Dimension.fillToConstraints
                },
                targetState = parentRes to childRes,
                animationSpec = getAnimateTween()
            ) { (parent, child) ->
                val style = MaterialTheme.typography.titleMedium
                val softWarp = false
                val maxLines = 1

                if (parent == null) {
                    ColoredBrandTitle(
                        style = style,
                        onClick = {
                            brandTapCount += 1
                            if (brandTapCount >= 7) {
                                brandTapCount = 0
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                showBrandEasterEgg = true
                            }
                        }
                    )
                } else {
                    val parentText = stringResource(parent)
                    val childText = child?.let { stringResource(it) }

                    Text(
                        text = if (childText != null) "$parentText - $childText" else parentText,
                        style = style,
                        softWrap = softWarp,
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier
                    .constrainAs(endButtons) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, margin = 12.dp)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(
                    visible = !(isTasksExpanded || taskRunning),
                    enter = slideInVertically(
                        animationSpec = getAnimateTween(),
                        initialOffsetY = { -50 }
                    ) + fadeIn(animationSpec = getAnimateTween()),
                    exit = slideOutVertically(
                        animationSpec = getAnimateTween(),
                        targetOffsetY = { -50 }
                    ) + fadeOut(animationSpec = getAnimateTween())
                ) {
                    Row(
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.large)
                            .clickable { changeExpandedState() }
                            .padding(all = 8.dp)
                            .width(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.size(22.dp),
                            painter = painterResource(R.drawable.ic_assignment_filled),
                            contentDescription = stringResource(R.string.main_task_menu)
                        )
                    }
                }

                TopBarRailItem(
                    selected = inDownloadScreen,
                    painter = painterResource(R.drawable.ic_download_2_filled),
                    text = stringResource(R.string.generic_download),
                    onClick = {
                        if (!inDownloadScreen) toDownloadScreen()
                    },
                )

                TopBarRailItem(
                    selected = inSettingsScreen,
                    painter = painterResource(R.drawable.ic_settings_filled),
                    text = stringResource(R.string.generic_setting),
                    onClick = {
                        if (!inSettingsScreen) toSettingsScreen()
                    },
                )
            }
        }
        }
    }
}

@Composable
private fun ColoredBrandTitle(
    style: TextStyle,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = BrandBlue)) { append("Z") }
            withStyle(SpanStyle(color = BrandRed)) { append("M") }
            withStyle(SpanStyle(color = BrandYellow)) { append("C") }
            withStyle(SpanStyle(color = BrandGreen)) { append("L") }
        },
        style = style,
        softWrap = false,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun TopBarRailItem(
    selected: Boolean,
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    var navigationPending by remember { mutableStateOf(false) }
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = getAnimateTween(),
        label = "TopBarActionContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = getAnimateTween(),
        label = "TopBarActionContent"
    )
    val actionWidth = if (LocalConfiguration.current.screenWidthDp < 720) 96.dp else 112.dp

    Surface(
        modifier = modifier
            .width(actionWidth)
            .height(48.dp)
            .motionPressScale(
                interactionSource = interactionSource,
                pressedScale = 0.975f,
                label = "TopBarActionScale"
            ),
        onClick = {
            if (!navigationPending) {
                navigationPending = true
                coroutineScope.launch {
                    try {
                        withFrameNanos { }
                        onClick()
                    } finally {
                        navigationPending = false
                    }
                }
            }
        },
        interactionSource = interactionSource,
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = if (selected) {
                Arrangement.spacedBy(6.dp, Alignment.Start)
            } else {
                Arrangement.Center
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painter,
                contentDescription = text
            )
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(animationSpec = getAnimateTween()),
                exit = fadeOut(animationSpec = getAnimateTween())
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = text,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    screenBackStackModel: ScreenBackStackViewModel,
    toMainScreen: () -> Unit,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val backStack = screenBackStackModel.mainScreen.backStack

    if (backStack.isNotEmpty()) {
        /** 导航至版本详细信息屏幕 */
        val navigateToVersions: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.navigateTo(
                screenKey = NestedNavKey.VersionSettings(version),
                useClassEquality = true
            )
        }
        /** 导航至整合包导出屏幕 */
        val navigateToExport: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.removeAndNavigateTo(
                remove = NestedNavKey.VersionSettings::class,
                screenKey = NestedNavKey.VersionExport(version),
                useClassEquality = true
            )
        }

        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(reverse = true),
            entryProvider = entryProvider {
                entry<NormalNavKey.LauncherMain> {
                    LauncherScreen(
                        backStackViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        onLaunchGame = { version ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.Launch.Game(version)
                            )
                        },
                        onOpenLink = {
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(it))
                        },
                        onHomePageEvent = { event ->
                            eventViewModel.sendEvent(EventViewModel.Event.HomePage.Event(event))
                        }
                    )
                }
                entry<NestedNavKey.Settings> { key ->
                    SettingsScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.License> { key ->
                    LicenseScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel
                    )
                }
                entry<NormalNavKey.AccountManager> { key ->
                    AccountManageScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        backToMainScreen = toMainScreen,
                        openLink = { url ->
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(url))
                        },
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.WebScreen> { key ->
                    WebViewScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NormalNavKey.VersionsManager> {
                    VersionsManageScreen(
                        backScreenViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        navigateToExport = navigateToExport,
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.FileSelector> { key ->
                    FileSelectorScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel
                    ) {
                        backStack.removeLastOrNull()
                    }
                }
                entry<NestedNavKey.VersionSettings> { key ->
                    VersionSettingsScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        backToMainScreen = toMainScreen,
                        onExportModpack = {
                            navigateToExport(key.version)
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NestedNavKey.VersionExport> { key ->
                    VersionExportScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        backToMainScreen = toMainScreen
                    )
                }
                entry<NestedNavKey.Download> { key ->
                    DownloadScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        modpackImportViewModel = modpackImportViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Multiplayer> {
                    MultiplayerScreen(
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NormalNavKey.HomePageEditor> {
                    HomePageEditorScreen(
                        backStackViewModel = screenBackStackModel,
                    )
                }
                entry<NormalNavKey.LogView> { key ->
                    LogViewScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}

@Composable
private fun TaskMenu(
    tasks: List<Task>,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    changeExpandedState: () -> Unit = {}
) {
    val show = isExpanded && tasks.isNotEmpty()

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    AnimatedVisibility(
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = { if (isRtl) it else -it },
            animationSpec = getAnimateTween()
        ) + fadeIn(animationSpec = getAnimateTween()),
        exit = slideOutHorizontally(
            targetOffsetX = { if (isRtl) it else -it },
            animationSpec = getAnimateTween()
        ) + fadeOut(animationSpec = getAnimateTween()),
        visible = show
    ) {
        BackgroundCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 6.dp),
            influencedByBackground = false,
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor(),
                contentColor = onBackgroundColor()
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column {
                CardTitleLayout(blur = 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.CenterStart),
                            onClick = changeExpandedState
                        ) {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(R.drawable.ic_arrow_left_rounded),
                                contentDescription = stringResource(R.string.generic_collapse)
                            )
                        }

                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(R.string.main_task_menu)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    items(
                        items = tasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskItem(
                            taskProgress = task.currentProgress,
                            taskMessageRes = task.currentMessageRes,
                            taskMessageArgs = task.currentMessageArgs,
                            taskRateBytesPerSec = task.currentRateBytesPerSec,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            //取消任务
                            TaskSystem.cancelTask(task.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    taskProgress: Float,
    taskMessageRes: Int?,
    taskMessageArgs: Array<out Any>?,
    taskRateBytesPerSec: Long,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
    onCancelClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically),
                onClick = onCancelClick
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.generic_cancel)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                taskMessageRes?.let { messageRes ->
                    Text(
                        text = if (taskMessageArgs != null) {
                            stringResource(messageRes, *taskMessageArgs)
                        } else {
                            stringResource(messageRes)
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                if (taskProgress < 0) { //负数则代表不确定
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { taskProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    taskProgress.takeIf { it >= 0f }?.let { progress ->
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    taskRateBytesPerSec.takeIf { it >= 0L }?.let { bytes ->
                        val text = remember(bytes) { "${formatFileSize(bytes)}/s" }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
