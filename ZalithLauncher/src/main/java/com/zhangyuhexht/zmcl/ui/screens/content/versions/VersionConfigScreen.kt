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

package com.zhangyuhexht.zmcl.ui.screens.content.versions

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.game.control.ControlManager
import com.zhangyuhexht.zmcl.game.multirt.RuntimesManager
import com.zhangyuhexht.zmcl.game.multirt.Runtime
import com.zhangyuhexht.zmcl.game.plugin.driver.Driver
import com.zhangyuhexht.zmcl.game.plugin.driver.DriverPluginManager
import com.zhangyuhexht.zmcl.game.renderer.RendererInterface
import com.zhangyuhexht.zmcl.game.renderer.Renderers
import com.zhangyuhexht.zmcl.game.support.touch_controller.VibrationHandler
import com.zhangyuhexht.zmcl.game.version.installed.GraphicsApi
import com.zhangyuhexht.zmcl.game.version.installed.Version
import com.zhangyuhexht.zmcl.game.version.installed.VersionConfig
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.setting.unit.floatRange
import com.zhangyuhexht.zmcl.setting.unit.getOrMin
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.components.AnimatedLazyColumn
import com.zhangyuhexht.zmcl.ui.components.IDItem
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.elements.MemoryPreview
import com.zhangyuhexht.zmcl.ui.screens.content.elements.MicrophoneCheckOperation
import com.zhangyuhexht.zmcl.ui.screens.content.elements.MicrophoneCheckState
import com.zhangyuhexht.zmcl.ui.screens.content.settings.DriverSummaryLayout
import com.zhangyuhexht.zmcl.ui.screens.content.settings.RendererSummaryLayout
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.CardPosition
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.EnumSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.ListSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SettingsCardColumn
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SimpleIDListCard
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.TextInputSettingsCard
import com.zhangyuhexht.zmcl.ui.screens.content.versions.layouts.StatefulDropdownMenuFollowGlobal
import com.zhangyuhexht.zmcl.ui.screens.content.versions.layouts.ToggleableIntSliderSettingsCard
import com.zhangyuhexht.zmcl.utils.animation.getAnimateEnterTransition
import com.zhangyuhexht.zmcl.utils.animation.getAnimateExitTransition
import com.zhangyuhexht.zmcl.utils.logging.Logger
import com.zhangyuhexht.zmcl.utils.platform.getMaxMemoryForSettings
import com.zhangyuhexht.zmcl.utils.string.getMessageOrToString
import com.zhangyuhexht.zmcl.viewmodel.ErrorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "VersionConfigScreen"

private data class VersionConfigRenderData(
    val renderers: List<RendererInterface>,
    val drivers: List<Driver>,
    val runtimes: List<Runtime>
)

@Composable
fun VersionConfigScreen(
    mainScreenKey: TitledNavKey?,
    versionsScreenKey: TitledNavKey?,
    version: Version,
    backToMainScreen: () -> Unit,
    onCheckVulkan: () -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    if (!version.isValid()) {
        backToMainScreen()
        return
    }

    val applicationContext = LocalContext.current.applicationContext
    val renderData by produceState<VersionConfigRenderData?>(
        initialValue = null,
        key1 = applicationContext
    ) {
        value = withContext(Dispatchers.IO) {
            VersionConfigRenderData(
                renderers = Renderers.getCompatibleRenderers(applicationContext).second,
                drivers = DriverPluginManager.getDriverList(),
                runtimes = RuntimesManager.getRuntimes().filter(Runtime::isCompatible)
            )
        }
    }

    BaseScreen(
        levels1 = listOf(
            Pair(NestedNavKey.VersionSettings::class.java, mainScreenKey)
        ),
        Triple(NormalNavKey.Versions.Config, versionsScreenKey, false)
    ) { isVisible ->
        val config = version.getVersionConfig()
        val data = renderData
        if (data == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@BaseScreen
        }

        AnimatedLazyColumn(
            modifier = Modifier.fillMaxSize(),
            isVisible = isVisible,
            contentPadding = PaddingValues(all = 12.dp)
        ) { scope ->
            animatedItem(scope, key = "version") { yOffset ->
                VersionConfigs(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) },
                    config = config,
                    renderers = data.renderers,
                    drivers = data.drivers,
                    submitError = submitError
                )
            }

            animatedItem(scope, key = "game") { yOffset ->
                GameConfigs(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) },
                    config = config,
                    runtimes = data.runtimes,
                    submitError = submitError
                )
            }

            animatedItem(scope, key = "support") { yOffset ->
                SupportConfigs(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) },
                    config = config,
                    onCheckVulkan = onCheckVulkan,
                    submitError = submitError
                )
            }
        }
    }
}

@Composable
private fun VersionConfigs(
    config: VersionConfig,
    renderers: List<RendererInterface>,
    drivers: List<Driver>,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    SettingsCardColumn(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = stringResource(R.string.versions_config_version_settings),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        StatefulDropdownMenuFollowGlobal(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Top,
            currentValue = config.isolationType,
            onValueChange = { type ->
                if (config.isolationType != type) {
                    config.isolationType = type
                    config.saveOrShowError(context, submitError)
                }
            },
            title = stringResource(R.string.versions_config_isolation_title),
            summary = stringResource(R.string.versions_config_isolation_summary)
        )

        StatefulDropdownMenuFollowGlobal(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            currentValue = config.skipGameIntegrityCheck,
            onValueChange = { type ->
                if (config.skipGameIntegrityCheck != type) {
                    config.skipGameIntegrityCheck = type
                    config.saveOrShowError(context, submitError)
                }
            },
            title = stringResource(R.string.settings_game_skip_game_integrity_check_title),
            summary = stringResource(R.string.settings_game_skip_game_integrity_check_summary)
        )

        val renderersIdList = getIDList(renderers) {
            IDItem(it.getUniqueIdentifier(), it.getRendererName())
        }
        ListSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            items = renderersIdList,
            currentId = config.renderer,
            defaultId = "",
            title = stringResource(R.string.versions_config_renderer),
            getItemText = { it.title },
            getItemId = { it.id },
            getItemSummary = { item ->
                renderers.find { it.getUniqueIdentifier() == item.id }?.let { renderer ->
                    RendererSummaryLayout(renderer)
                }
            },
            onValueChange = { item ->
                if (config.renderer != item.id) {
                    config.renderer = item.id
                    config.saveOrShowError(context, submitError)
                }
            }
        )

        val driversIdList = getIDList(drivers) {
            IDItem(it.id, it.name)
        }
        ListSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            items = driversIdList,
            currentId = config.driver,
            defaultId = "",
            title = stringResource(R.string.versions_config_vulkan_driver),
            getItemText = { it.title },
            getItemId = { it.id },
            getItemSummary = { item ->
                drivers.find { it.id == item.id }?.let { driver ->
                    DriverSummaryLayout(driver)
                }
            },
            onValueChange = { item ->
                if (config.driver != item.id) {
                    config.driver = item.id
                    config.saveOrShowError(context, submitError)
                }
            }
        )

        val graphicsApis = GraphicsApi.entries
        val defaultGraphicsTitle = stringResource(R.string.settings_game_graphics_api_default)
        val defaultOpenGLTitle = stringResource(R.string.settings_game_graphics_api_default_opengl)
        val graphicsApisList = getIDList(graphicsApis) {
            val title = when (it) {
                GraphicsApi.DEFAULT -> defaultGraphicsTitle
                GraphicsApi.DEFAULT_OPENGL -> defaultOpenGLTitle
                else -> it.displayName
            }
            IDItem(it.name, title)
        }
        ListSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            items = graphicsApisList,
            currentId = config.graphicsApi?.name ?: "",
            defaultId = "",
            title = stringResource(R.string.settings_game_graphics_api_title),
            summary = stringResource(R.string.settings_game_graphics_api_summary),
            getItemText = { it.title },
            getItemId = { it.id },
            onValueChange = { item ->
                if (config.graphicsApi?.name != item.id) {
                    config.graphicsApi = GraphicsApi.entries.find { it.name == item.id }
                    config.saveOrShowError(context, submitError)
                }
            }
        )

        val controls by ControlManager.dataList.collectAsStateWithLifecycle()
        val controlsIdList = getIDList(controls.filter { it.isSupport }) {
            IDItem(it.file.name, it.controlLayout.info.name.translate())
        }
        ListSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Bottom,
            items = controlsIdList,
            currentId = config.control,
            defaultId = "",
            title = stringResource(R.string.versions_config_control),
            getItemText = { it.title },
            getItemId = { it.id },
            onValueChange = {
                if (config.control != it.id) {
                    config.control = it.id
                    config.saveOrShowError(context, submitError)
                }
            }
        )
    }
}

@Composable
private fun GameConfigs(
    config: VersionConfig,
    runtimes: List<Runtime>,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    SettingsCardColumn(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = stringResource(R.string.versions_config_game_settings),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        SimpleIDListCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Top,
            items = getIDList(runtimes) { IDItem(it.name, it.name) },
            currentId = config.javaRuntime,
            defaultId = "",
            title = stringResource(R.string.settings_game_java_runtime_title),
            summary = stringResource(R.string.versions_config_java_runtime_summary),
            onValueChange = { item ->
                if (config.javaRuntime != item.id) {
                    config.javaRuntime = item.id
                    config.saveOrShowError(context, submitError)
                }
            }
        )

        /**
         * 临时已分配内存，用于UI状态更新
         */
        var ramAllocation by remember { mutableIntStateOf(config.ramAllocation) }
        ToggleableIntSliderSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            currentValue = config.ramAllocation,
            valueRange = AllSettings.ramAllocation.floatRange.start..getMaxMemoryForSettings(LocalContext.current).toFloat(),
            defaultValue = AllSettings.ramAllocation.getOrMin(),
            title = stringResource(R.string.settings_game_java_memory_title),
            summary = stringResource(R.string.settings_game_java_memory_summary),
            suffix = "MB",
            onValueChange = {
                config.ramAllocation = it
                ramAllocation = it
            },
            onValueChangeFinished = { config.saveOrShowError(context, submitError) },
            previewContent = {
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = ramAllocation >= 256,
                    enter = getAnimateEnterTransition(),
                    exit = getAnimateExitTransition()
                ) {
                    MemoryPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp, end = 8.dp),
                        preview = ramAllocation.takeIf { it >= 256 }?.toDouble(),
                        usedText = { usedMemory, totalMemory ->
                            stringResource(R.string.settings_game_java_memory_used_text, usedMemory.toInt(), totalMemory.toInt())
                        },
                        previewText = { preview ->
                            stringResource(R.string.settings_game_java_memory_allocation_text, preview.toInt())
                        }
                    )
                }
            }
        )

        var customInfo by remember { mutableStateOf(config.customInfo) }
        TextInputSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            value = customInfo,
            onValueChange = { value ->
                customInfo = value
                if (config.customInfo != value) {
                    config.customInfo = value
                    config.saveOrShowError(context, submitError)
                }
            },
            title = stringResource(R.string.settings_game_version_custom_info_title),
            summary = stringResource(R.string.settings_game_version_custom_info_summary),
            label = {
                Text(text = stringResource(R.string.versions_config_follow_global_if_blank))
            }
        )

        var jvmArgs by remember { mutableStateOf(config.jvmArgs) }
        TextInputSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            value = jvmArgs,
            title = stringResource(R.string.settings_game_jvm_args_title),
            summary = stringResource(R.string.settings_game_jvm_args_summary),
            onValueChange = { value ->
                jvmArgs = value
                if (config.jvmArgs != value) {
                    config.jvmArgs = value
                    config.saveOrShowError(context, submitError)
                }
            },
            label = {
                Text(text = stringResource(R.string.versions_config_follow_global_if_blank))
            }
        )

        var serverIp by remember { mutableStateOf(config.serverIp) }
        TextInputSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Bottom,
            value = serverIp,
            title = stringResource(R.string.versions_config_auto_join_server_ip_title),
            summary = stringResource(R.string.versions_config_auto_join_server_ip_summary),
            onValueChange = { value ->
                serverIp = value
                if (config.serverIp != value) {
                    config.serverIp = value
                    config.saveOrShowError(context, submitError)
                }
            },
            label = {
                Text(text = stringResource(R.string.versions_config_disable_if_blank))
            }
        )
    }
}

@Composable
private fun SupportConfigs(
    config: VersionConfig,
    onCheckVulkan: () -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    SettingsCardColumn(modifier = modifier) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = stringResource(R.string.versions_config_support_settings),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        val vibrator = remember(context) { context.getSystemService<Vibrator>() }
        var touchVibrateKind by remember { mutableStateOf(config.touchVibrateKind) }
        val effectiveVibrateKind = touchVibrateKind ?: VibrationHandler.VibrateKind.default
        var touchVibrateDuration by remember { mutableIntStateOf(config.touchVibrateDuration) }
        EnumSettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Top,
            value = effectiveVibrateKind,
            title = stringResource(R.string.versions_config_vibrate_kind_title),
            summary = stringResource(R.string.versions_config_vibrate_kind_summary),
            entries = VibrationHandler.VibrateKind.entries,
            getRadioEnable = { enum ->
                if (enum == VibrationHandler.VibrateKind.ONE_SHOT) {
                    true
                } else {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                }
            },
            getRadioText = { enum ->
                when (enum) {
                    VibrationHandler.VibrateKind.ONE_SHOT -> stringResource(R.string.vibrate_kind_one_shot)
                    VibrationHandler.VibrateKind.CLICK -> stringResource(R.string.vibrate_kind_click)
                    VibrationHandler.VibrateKind.DOUBLE_CLICK -> stringResource(R.string.vibrate_kind_double_click)
                    VibrationHandler.VibrateKind.HEAVY_CLICK -> stringResource(R.string.vibrate_kind_heavy_click)
                    VibrationHandler.VibrateKind.TICK -> stringResource(R.string.vibrate_kind_tick)
                }
            },
            maxItemsInEachRow = 4,
            onRadioClick = { enum ->
                touchVibrateKind = enum
                vibrator?.let { vibrator ->
                    VibrationHandler.vibrate(
                        vibrator = vibrator,
                        vibrateDuration = touchVibrateDuration,
                        vibrateKind = touchVibrateKind,
                    )
                }
                if (config.touchVibrateKind != enum) {
                    config.touchVibrateKind = enum
                    config.saveOrShowError(context, submitError)
                }
            }
        )

        AnimatedVisibility(
            visible = effectiveVibrateKind == VibrationHandler.VibrateKind.ONE_SHOT,
            enter = getAnimateEnterTransition(),
            exit = getAnimateExitTransition()
        ) {
            IntSliderSettingsCard(
                modifier = Modifier.fillMaxWidth(),
                position = CardPosition.Middle,
                value = touchVibrateDuration,
                title = stringResource(R.string.versions_config_vibrate_duration_title),
                summary = stringResource(R.string.versions_config_vibrate_duration_summary),
                valueRange = 80f..500f,
                onValueChange = {
                    touchVibrateDuration = it
                    config.touchVibrateDuration = touchVibrateDuration
                },
                onValueChangeFinished = {
                    vibrator?.let { vibrator ->
                        VibrationHandler.vibrate(
                            vibrator = vibrator,
                            vibrateDuration = touchVibrateDuration,
                            vibrateKind = touchVibrateKind,
                        )
                    }
                    config.saveOrShowError(context, submitError)
                },
                suffix = "ms",
                fineTuningControl = true
            )
        }

        SettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Middle,
            title = stringResource(R.string.game_vulkan_check_title),
            summary = stringResource(R.string.game_vulkan_check_text),
            onClick = onCheckVulkan
        )

        //检查麦克风
        var microphoneState by remember { mutableStateOf<MicrophoneCheckState>(MicrophoneCheckState.None) }
        MicrophoneCheckOperation(
            state = microphoneState,
            changeState = { microphoneState = it }
        )
        SettingsCard(
            modifier = Modifier.fillMaxWidth(),
            position = CardPosition.Bottom,
            title = stringResource(R.string.versions_config_microphone_check_title),
            summary = stringResource(R.string.versions_config_microphone_check_summary),
            onClick = {
                microphoneState = MicrophoneCheckState.Start
            }
        )
    }
}

@Composable
private fun <E> getIDList(list: List<E>, toIDItem: (E) -> IDItem): List<IDItem> {
    return list.map {
        toIDItem(it)
    }.toMutableList().apply {
        add(0, IDItem("", stringResource(R.string.generic_follow_global)))
    }
}

private fun VersionConfig.saveOrShowError(
    context: Context,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    runCatching {
        saveWithThrowable()
    }.onFailure { e ->
        Logger.error(TAG, "Failed to save version config!", e)
        submitError(
            ErrorViewModel.ThrowableMessage(
                title = context.getString(R.string.versions_config_failed_to_save),
                message = e.getMessageOrToString()
            )
        )
    }
}
