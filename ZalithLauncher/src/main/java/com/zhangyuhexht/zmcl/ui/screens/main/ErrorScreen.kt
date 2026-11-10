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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.BuildKeys
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.ui.activities.CrashType
import com.zhangyuhexht.zmcl.ui.components.BackgroundCard
import com.zhangyuhexht.zmcl.ui.components.MarqueeText
import com.zhangyuhexht.zmcl.ui.components.ScalingActionButton
import com.zhangyuhexht.zmcl.ui.theme.backgroundColor
import com.zhangyuhexht.zmcl.ui.theme.onBackgroundColor

@Composable
fun ErrorScreen(
    crashType: CrashType,
    shareLogs: Boolean = true,
    canUpload: Boolean = false,
    canRestart: Boolean = true,
    onShareLogsClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onRestartClick: () -> Unit = {},
    onExitClick: () -> Unit = {},
    body: @Composable ColumnScope.() -> Unit
) {
    ErrorScreenLandscape(
        crashType = crashType,
        shareLogs = shareLogs,
        canUpload = canUpload,
        canRestart = canRestart,
        onShareLogsClick = onShareLogsClick,
        onUploadClick = onUploadClick,
        onRestartClick = onRestartClick,
        onExitClick = onExitClick,
        body = body
    )
}

@Composable
private fun ErrorScreenLandscape(
    crashType: CrashType,
    shareLogs: Boolean,
    canUpload: Boolean,
    canRestart: Boolean,
    onShareLogsClick: () -> Unit,
    onUploadClick: () -> Unit,
    onRestartClick: () -> Unit,
    onExitClick: () -> Unit,
    body: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                crashType = crashType
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor(),
        contentColor = onBackgroundColor(),
        contentWindowInsets = if (AllSettings.launcherFullScreen.state) {
            WindowInsets()
        } else {
            WindowInsets.safeContent.only(WindowInsetsSides.Horizontal)
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ErrorContent(
                modifier = Modifier.weight(7f),
                body = body
            )

            ActionLayout(
                modifier = Modifier.weight(3f),
                crashType = crashType,
                shareLogs = shareLogs,
                canUpload = canUpload,
                canRestart = canRestart,
                onShareLogsClick = onShareLogsClick,
                onUploadClick = onUploadClick,
                onRestartClick = onRestartClick,
                onExitClick = onExitClick
            )
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    crashType: CrashType,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        val text = when (crashType) {
            //闁革负鍔岄幆搴ㄥ礉閵娿儲鐝ょ€规洍鏅滅花婵嬫儍閸曨剚顦ч柛濠冪懕缁辨繈寮伴崜褋浠涘☉鎾亾濞戞搩浜ｇ欢婵囩▔鏉炴澘绱梺鎻掔Ф濞堟垿寮介崶顒夋毌
            CrashType.LAUNCHER_CRASH -> stringResource(R.string.crash_launcher_title, BuildKeys.LAUNCHER_NAME)
            //婵炴挸鎲￠崹娆愭交閹邦垼鏀界€规洍鏅滅花婵囩閸☆厾绀夊鍫嗗嫷娲ら柛婊冭嫰閹酣宕濋妸銉︾彜闁稿繐纾柈瀛樼▔瀹ュ懌浜ｉ柨娑樺缁海浠﹂弴鐘粵閹煎瓨姊婚弫銈夊冀閸ヮ剦鏆?
            CrashType.GAME_CRASH -> BuildKeys.LAUNCHER_NAME
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = text
        )
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    body: @Composable ColumnScope.() -> Unit
) {
    BackgroundCard(
        modifier = modifier,
        influencedByBackground = false,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = body
        )
    }
}

@Composable
private fun ActionLayout(
    modifier: Modifier = Modifier,
    crashType: CrashType,
    shareLogs: Boolean,
    canUpload: Boolean,
    canRestart: Boolean,
    onShareLogsClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onRestartClick: () -> Unit = {},
    onExitClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(
                    R.string.crash_type,
                    stringResource(crashType.textRes)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom)
        ) {
            if (canUpload) {
                ScalingActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onUploadClick
                ) {
                    MarqueeText(text = stringResource(R.string.crash_link_share_button))
                }
            }
            if (shareLogs) {
                ScalingActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onShareLogsClick
                ) {
                    MarqueeText(text = stringResource(R.string.crash_share_logs))
                }
            }
            if (canRestart) {
                ScalingActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestartClick
                ) {
                    MarqueeText(text = stringResource(R.string.crash_restart))
                }
            }
            ScalingActionButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onExitClick
            ) {
                MarqueeText(text = stringResource(R.string.crash_exit))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewErrorScreen() {
    MaterialExpressiveTheme {
        ErrorScreen(
            crashType = CrashType.LAUNCHER_CRASH,
            shareLogs = true,
            canUpload = true,
            canRestart = true,
            onShareLogsClick = {},
            onUploadClick = {},
            onRestartClick = {},
            onExitClick = {},
            body = {}
        )
    }
}

