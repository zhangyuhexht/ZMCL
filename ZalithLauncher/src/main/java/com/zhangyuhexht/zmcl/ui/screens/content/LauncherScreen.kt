/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.ui.screens.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.game.account.Account
import com.zhangyuhexht.zmcl.game.account.AccountsManager
import com.zhangyuhexht.zmcl.game.version.installed.Version
import com.zhangyuhexht.zmcl.game.version.installed.VersionsManager
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.elements.PlayerFace
import com.zhangyuhexht.zmcl.ui.screens.content.elements.VersionIconImage
import com.zhangyuhexht.zmcl.ui.screens.main.custom_home.MarkdownBlock
import com.zhangyuhexht.zmcl.viewmodel.ScreenBackStackViewModel

private val GoogleBlue = Color(0xFF4285F4)
private val GoogleGreen = Color(0xFF34A853)
private val GoogleYellow = Color(0xFFFBBC05)
private val GoogleRed = Color(0xFFEA4335)

@Composable
fun LauncherScreen(
    backStackViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    onLaunchGame: (Version?) -> Unit,
    onOpenLink: (String) -> Unit,
    onHomePageEvent: (MarkdownBlock.Button.Event) -> Unit,
) {
    BaseScreen(
        screenKey = NormalNavKey.LauncherMain,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) {
        CompositionLocalProvider(
            LocalUriHandler provides object : UriHandler {
                override fun openUri(uri: String) = onOpenLink(uri)
            }
        ) {
            val toAccountManageScreen: () -> Unit = {
                backStackViewModel.mainScreen.navigateTo(
                    screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                )
            }
            val toVersionManageScreen: () -> Unit = {
                backStackViewModel.mainScreen.removeAndNavigateTo(
                    remove = NestedNavKey.VersionSettings::class,
                    screenKey = NormalNavKey.VersionsManager
                )
            }
            val toVersionSettingsScreen: () -> Unit = {
                VersionsManager.currentVersion.value?.let(navigateToVersions)
            }

            GoogleLauncherHome(
                onLaunchGame = onLaunchGame,
                toAccountManageScreen = toAccountManageScreen,
                toVersionManageScreen = toVersionManageScreen,
                toVersionSettingsScreen = toVersionSettingsScreen
            )
        }
    }
}

@Composable
private fun GoogleLauncherHome(
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GoogleBackground()
        AdaptiveGoogleLayout(
            onLaunchGame = onLaunchGame,
            toAccountManageScreen = toAccountManageScreen,
            toVersionManageScreen = toVersionManageScreen,
            toVersionSettingsScreen = toVersionSettingsScreen
        )
    }
}

@Composable
private fun AdaptiveGoogleLayout(
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val dense = maxHeight < 460.dp || maxWidth < 700.dp
        val contentPadding = when {
            maxWidth >= 1200.dp -> 24.dp
            dense -> 12.dp
            else -> 18.dp
        }
        val spacing = if (dense) 10.dp else 16.dp
        val leftWeight = when {
            maxWidth >= 1200.dp -> 1.35f
            maxWidth < 700.dp -> 1.05f
            else -> 1.2f
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = contentPadding, vertical = if (dense) 10.dp else 16.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(leftWeight),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                AccountCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    dense = dense,
                    onClick = toAccountManageScreen
                )
                CurrentVersionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    dense = dense,
                    toVersionManageScreen = toVersionManageScreen
                )
            }

            LaunchWorkspace(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.8f),
                dense = dense,
                onLaunchGame = onLaunchGame,
                toVersionSettingsScreen = toVersionSettingsScreen
            )
        }
    }
}

@Composable
private fun GoogleBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
    ) {
        listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen).forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
private fun LaunchWorkspace(
    modifier: Modifier = Modifier,
    dense: Boolean = false,
    onLaunchGame: (Version?) -> Unit,
    toVersionSettingsScreen: () -> Unit
) {
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = modifier) {
        val stageSize = minOf(
            if (dense) 132.dp else 214.dp,
            maxWidth * if (dense) 0.66f else 0.72f,
            maxHeight * if (dense) 0.34f else 0.38f
        )

        ElevatedCard(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(if (dense) 24.dp else 32.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (dense) 12.dp else 22.dp),
                verticalArrangement = Arrangement.spacedBy(if (dense) 8.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LauncherIconStage(size = stageSize)
                }
                LaunchControls(
                    modifier = Modifier.fillMaxWidth(),
                    dense = dense,
                    version = version,
                    isRefreshing = isRefreshing,
                    onLaunchGame = onLaunchGame,
                    toVersionSettingsScreen = toVersionSettingsScreen
                )
            }
        }
    }
}

@Composable
private fun LaunchControls(
    modifier: Modifier = Modifier,
    dense: Boolean,
    version: Version?,
    isRefreshing: Boolean,
    onLaunchGame: (Version?) -> Unit,
    toVersionSettingsScreen: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (dense) 6.dp else 18.dp)
    ) {
        AssistChip(
            onClick = {},
            label = {
                Text(
                    text = if (isRefreshing) {
                        stringResource(R.string.home_status_syncing)
                    } else {
                        stringResource(R.string.home_status_ready)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isRefreshing) GoogleYellow else GoogleGreen)
                )
            }
        )
        Text(
            text = version?.getVersionName() ?: stringResource(R.string.versions_manage_no_versions),
            style = if (dense) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            maxLines = if (dense) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = version.safeSummary(stringResource(R.string.home_no_version_summary)),
            style = if (dense) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (dense) 1 else 3,
            overflow = TextOverflow.Ellipsis
        )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (!dense && maxWidth < 360.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LaunchButton(
                        modifier = Modifier.fillMaxWidth(),
                        dense = dense,
                        version = version,
                        isRefreshing = isRefreshing,
                        onLaunchGame = onLaunchGame
                    )
                    SettingsButton(
                        modifier = Modifier.fillMaxWidth(),
                        dense = dense,
                        version = version,
                        toVersionSettingsScreen = toVersionSettingsScreen
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LaunchButton(
                        modifier = Modifier.weight(1f),
                        dense = dense,
                        version = version,
                        isRefreshing = isRefreshing,
                        onLaunchGame = onLaunchGame
                    )
                    SettingsButton(
                        modifier = Modifier.weight(1f),
                        dense = dense,
                        version = version,
                        toVersionSettingsScreen = toVersionSettingsScreen
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchButton(
    modifier: Modifier,
    dense: Boolean,
    version: Version?,
    isRefreshing: Boolean,
    onLaunchGame: (Version?) -> Unit
) {
    Button(
        modifier = modifier.height(if (dense) 48.dp else 56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
        enabled = !isRefreshing && version?.isValid() == true,
        onClick = { onLaunchGame(null) }
    ) {
        Icon(
            modifier = Modifier.size(if (dense) 18.dp else 24.dp),
            painter = painterResource(R.drawable.ic_rocket_launch_filled),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(if (dense) 6.dp else 10.dp))
        Text(
            text = stringResource(R.string.main_launch_game),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SettingsButton(
    modifier: Modifier,
    dense: Boolean,
    version: Version?,
    toVersionSettingsScreen: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.height(if (dense) 48.dp else 56.dp),
        shape = RoundedCornerShape(18.dp),
        enabled = version?.isValid() == true,
        onClick = toVersionSettingsScreen
    ) {
        Icon(
            modifier = Modifier.size(if (dense) 18.dp else 24.dp),
            painter = painterResource(R.drawable.ic_settings_filled),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(if (dense) 6.dp else 10.dp))
        Text(
            text = stringResource(R.string.versions_manage_settings),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LauncherIconStage(size: Dp = 238.dp) {
    val launcherIcon = AllSettings.launcherIcon.state

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.92f)
                .clip(RoundedCornerShape(size * 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            launcherIcon.previewBackgroundRes?.let { backgroundRes ->
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(backgroundRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(launcherIcon.previewForegroundRes),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun AccountCard(
    modifier: Modifier = Modifier,
    dense: Boolean = false,
    onClick: () -> Unit
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val defaultAccount = remember { Account(username = "Steve") }
    val displayedAccount = account ?: defaultAccount

    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (dense) 14.dp else 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (dense) 12.dp else 18.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(if (dense) 54.dp else 72.dp),
                shape = RoundedCornerShape(if (dense) 16.dp else 20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                PlayerFace(
                    modifier = Modifier.clip(RoundedCornerShape(if (dense) 16.dp else 20.dp)),
                    account = displayedAccount,
                    avatarSize = if (dense) 54 else 72
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (dense) 3.dp else 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_account_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = account?.username ?: stringResource(R.string.home_account_manage),
                    style = if (dense) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = account?.accountType?.toString() ?: stringResource(R.string.home_account_manage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right_rounded),
                contentDescription = stringResource(R.string.home_account_manage),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CurrentVersionCard(
    modifier: Modifier = Modifier,
    dense: Boolean = false,
    toVersionManageScreen: () -> Unit
) {
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()

    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = toVersionManageScreen),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (dense) 14.dp else 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (dense) 12.dp else 18.dp)
        ) {
            Surface(
                modifier = Modifier.size(if (dense) 64.dp else 84.dp),
                shape = RoundedCornerShape(if (dense) 18.dp else 24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                VersionIconImage(
                    version = version,
                    modifier = Modifier.padding(if (dense) 10.dp else 14.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (dense) 3.dp else 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_current_version_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isRefreshing) {
                        stringResource(R.string.home_status_syncing)
                    } else {
                        version?.getVersionName() ?: stringResource(R.string.versions_manage_no_versions)
                    },
                    style = if (dense) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = version.safeSummary(stringResource(R.string.home_no_version_summary)),
                    style = if (dense) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (dense) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right_rounded),
                contentDescription = stringResource(R.string.home_manage_versions),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Version?.safeSummary(fallback: String): String {
    return this?.takeIf { it.isValid() }?.runCatching { getVersionSummary() }?.getOrNull()
        ?: fallback
}
