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
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.zhangyuhexht.zmcl.ui.screens.content.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.BuildConfig
import com.zhangyuhexht.zmcl.BuildKeys
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.components.AnimatedLazyColumn
import com.zhangyuhexht.zmcl.ui.components.CardTitleLayout
import com.zhangyuhexht.zmcl.ui.components.ZmclEasterEggDialog
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.CardPosition
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SettingsCard
import com.zhangyuhexht.zmcl.ui.theme.itemColor
import com.zhangyuhexht.zmcl.ui.theme.onItemColor
import kotlinx.coroutines.delay

private val AboutHeroIconSize = 56.dp

@Composable
fun AboutInfoScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    openLink: (url: String) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    var developerTapCount by remember { mutableIntStateOf(0) }
    var showDeveloperEasterEgg by remember { mutableStateOf(false) }

    LaunchedEffect(developerTapCount) {
        if (developerTapCount > 0) {
            val expectedTapCount = developerTapCount
            delay(1600L)
            if (developerTapCount == expectedTapCount) developerTapCount = 0
        }
    }

    if (showDeveloperEasterEgg) {
        ZmclEasterEggDialog(
            title = stringResource(R.string.easter_egg_developer_title),
            message = stringResource(R.string.easter_egg_developer_message),
            onDismiss = { showDeveloperEasterEgg = false }
        )
    }

    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.AboutInfo, settingsScreenKey, false)
    ) { isVisible ->
        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(all = 8.dp)
        ) { scope ->
            animatedItem(scope) { yOffset ->
                AboutChunk(
                    modifier = Modifier.offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) },
                    title = stringResource(R.string.about_launcher_title)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AboutIdentityItem(
                            icon = painterResource(R.drawable.img_avatar_zhangyuhexht),
                            title = stringResource(R.string.about_zmcl_creator_title),
                            text = stringResource(R.string.about_zmcl_creator_text),
                            meta = stringResource(
                                R.string.about_launcher_version,
                                BuildConfig.VERSION_NAME
                            ),
                            onClick = {
                                developerTapCount += 1
                                if (developerTapCount >= 5) {
                                    developerTapCount = 0
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showDeveloperEasterEgg = true
                                }
                            }
                        )

                        AboutIdentityItem(
                            icon = painterResource(R.drawable.img_launcher_zmcl),
                            title = stringResource(R.string.about_zmcl_origin_title),
                            text = stringResource(R.string.about_zmcl_origin_text),
                            meta = BuildKeys.LAUNCHER_NAME,
                            action = {
                                Button(onClick = { openLink(BuildKeys.URL_HOME) }) {
                                    Text(
                                        text = stringResource(R.string.about_official_website),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutChunk(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    SettingsCard(
        modifier = modifier,
        position = CardPosition.Single
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardTitleLayout {
                Text(
                    modifier = Modifier.padding(all = 12.dp),
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                content = { content() }
            )
        }
    }
}

@Composable
private fun AboutIdentityItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    text: String,
    meta: String,
    action: @Composable RowScope.() -> Unit = {},
    onClick: (() -> Unit)? = null,
    color: Color = itemColor(),
    contentColor: Color = onItemColor()
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = onClick)
            } else {
                Modifier
            }
        ),
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(AboutHeroIconSize)
                    .clip(MaterialTheme.shapes.large),
                painter = icon,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.alpha(0.68f),
                    text = meta,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = action
            )
        }
    }
}
