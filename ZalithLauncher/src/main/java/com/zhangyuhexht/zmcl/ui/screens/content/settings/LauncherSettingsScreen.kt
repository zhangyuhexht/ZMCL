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

package com.zhangyuhexht.zmcl.ui.screens.content.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.coroutine.Task
import com.zhangyuhexht.zmcl.coroutine.TaskSystem
import com.zhangyuhexht.zmcl.path.PathManager
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.setting.enums.AppLanguage
import com.zhangyuhexht.zmcl.setting.enums.LauncherIcon
import com.zhangyuhexht.zmcl.setting.enums.MirrorSourceType
import com.zhangyuhexht.zmcl.setting.enums.applyLanguage
import com.zhangyuhexht.zmcl.setting.unit.floatRange
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.components.AnimatedLazyColumn
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.CardPosition
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.ListSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.MotionLevelSliderSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SettingsCardColumn
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SwitchSettingsCard
import com.zhangyuhexht.zmcl.utils.file.shareFile
import com.zhangyuhexht.zmcl.utils.isChinaMainland
import com.zhangyuhexht.zmcl.utils.logging.Logger
import java.io.File

private const val TAG = "LauncherSettingsScreen"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LauncherSettingsScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
) {
    val context = LocalContext.current

    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.Launcher, settingsScreenKey, false)
    ) { isVisible ->
        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(all = 12.dp)
        ) { scope ->
            animatedItem(scope) { yOffset ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) }
                ) {
                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.launcherLanguage,
                        items = AppLanguage.entries,
                        title = stringResource(R.string.settings_launcher_language),
                        getItemText = { stringResource(it.textRes) },
                        onValueChange = {
                            applyLanguage(it)
                        }
                    )

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.launcherIcon,
                        items = LauncherIcon.entries,
                        title = stringResource(R.string.launcher_icon_title),
                        summary = stringResource(R.string.launcher_icon_summary),
                        getItemText = { stringResource(it.labelRes) }
                    )

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        unit = AllSettings.launcherFullScreen,
                        title = stringResource(R.string.settings_launcher_full_screen_title),
                        summary = stringResource(R.string.settings_launcher_full_screen_summary)
                    )
                }
            }

            //动画设置板块
            animatedItem(scope) { yOffset ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) }
                ) {
                    MotionLevelSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Single,
                        unit = AllSettings.launcherMotionLevel,
                        title = stringResource(R.string.settings_launcher_swap_animate_type_title)
                    )
                }
            }

            animatedItem(scope) { yOffset ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) }
                ) {
                    //这些镜像源都是为了改善中国大陆内陆的网络环境而存在的
                    //境外不需要这些镜像源，反而可能拖慢境外的下载速度
                    //所以不应该向中国境外开放这些选项
                    val isChinaMainland = remember { isChinaMainland() }
                    if (isChinaMainland) {
                        ListSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Top,
                            unit = AllSettings.fetchModLoaderSource,
                            items = MirrorSourceType.entries,
                            title = stringResource(R.string.settings_launcher_mirror_modloader_title),
                            getItemText = { stringResource(it.textRes) }
                        )

                        ListSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Middle,
                            unit = AllSettings.fileDownloadSource,
                            items = MirrorSourceType.entries,
                            title = stringResource(R.string.settings_launcher_mirror_file_download_title),
                            getItemText = { stringResource(it.textRes) }
                        )

                        ListSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Middle,
                            unit = AllSettings.assetSearchSource,
                            items = MirrorSourceType.entries,
                            title = stringResource(R.string.settings_launcher_mirror_assets_search_title),
                            getItemText = { stringResource(it.textRes) }
                        )

                        ListSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Middle,
                            unit = AllSettings.assetDownloadSource,
                            items = MirrorSourceType.entries,
                            title = stringResource(R.string.settings_launcher_mirror_assets_download_title),
                            getItemText = { stringResource(it.textRes) }
                        )
                    }

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = if (isChinaMainland) {
                            CardPosition.Middle
                        } else {
                            CardPosition.Top
                        },
                        unit = AllSettings.launcherLogRetentionDays,
                        title = stringResource(R.string.settings_launcher_log_retention_days_title),
                        summary = stringResource(R.string.settings_launcher_log_retention_days_summary),
                        valueRange = AllSettings.launcherLogRetentionDays.floatRange,
                        suffix = stringResource(R.string.unit_day)
                    )

                    SettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        title = stringResource(R.string.settings_launcher_log_share_title),
                        summary = stringResource(R.string.settings_launcher_log_share_summary),
                        onClick = {
                            TaskSystem.submitTask(
                                Task.runTask(
                                    id = "ZIP_LOGS",
                                    task = { task ->
                                        task.updateProgress(-1f, R.string.settings_launcher_log_share_packing)
                                        val logsFile = File(PathManager.DIR_CACHE, "logs.zip")
                                        Logger.pack(logsFile)
                                        task.updateProgress(1f, null)
                                        //分享压缩包
                                        shareFile(
                                            context = context,
                                            file = logsFile
                                        )
                                    },
                                    onError = { e ->
                                        Logger.error(TAG, "Failed to package log files.", e)
                                    }
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
