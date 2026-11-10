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

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhangyuhexht.zmcl.R
import com.zhangyuhexht.zmcl.ZMCLApplication
import com.zhangyuhexht.zmcl.context.getFileName
import com.zhangyuhexht.zmcl.coroutine.Task
import com.zhangyuhexht.zmcl.coroutine.TaskSystem
import com.zhangyuhexht.zmcl.game.launch.executeJarWithUri
import com.zhangyuhexht.zmcl.game.multirt.Runtime
import com.zhangyuhexht.zmcl.game.multirt.RuntimesManager
import com.zhangyuhexht.zmcl.path.PathManager
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.ui.base.BaseScreen
import com.zhangyuhexht.zmcl.ui.components.CardTitleLayout
import com.zhangyuhexht.zmcl.ui.components.IconTextButton
import com.zhangyuhexht.zmcl.ui.components.SimpleAlertDialog
import com.zhangyuhexht.zmcl.ui.screens.NestedNavKey
import com.zhangyuhexht.zmcl.ui.screens.NormalNavKey
import com.zhangyuhexht.zmcl.ui.screens.TitledNavKey
import com.zhangyuhexht.zmcl.ui.screens.content.elements.ImportMultipleFileButton
import com.zhangyuhexht.zmcl.ui.screens.content.elements.ImportSingleFileButton
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.CardPosition
import com.zhangyuhexht.zmcl.ui.screens.content.settings.layouts.SettingsCard
import com.zhangyuhexht.zmcl.ui.theme.itemColor
import com.zhangyuhexht.zmcl.ui.theme.onItemColor
import com.zhangyuhexht.zmcl.utils.animation.swapAnimateDpAsState
import com.zhangyuhexht.zmcl.utils.device.Architecture
import com.zhangyuhexht.zmcl.utils.file.checkExtensionOrThrow
import com.zhangyuhexht.zmcl.utils.string.getMessageOrToString
import com.zhangyuhexht.zmcl.utils.string.throwableToString
import com.zhangyuhexht.zmcl.viewmodel.ErrorViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed interface RuntimeOperation {
    data object None: RuntimeOperation
    data class PreDelete(val runtime: Runtime): RuntimeOperation
    data class Delete(val runtime: Runtime): RuntimeOperation
}

private data class JavaManageUiState(
    val runtimes: List<Runtime> = emptyList(),
    val initialized: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: Throwable? = null
)

private class JavaManageViewModel : ViewModel() {
    private val refreshRequests = Channel<Boolean>(capacity = Channel.CONFLATED)
    private val _uiState = MutableStateFlow(JavaManageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshRequests.send(false)
            for (forceLoad in refreshRequests) {
                _uiState.update { it.copy(isRefreshing = true) }
                try {
                    val runtimes = withContext(Dispatchers.IO) {
                        RuntimesManager.getRuntimes(forceLoad = forceLoad)
                    }
                    _uiState.update {
                        it.copy(
                            runtimes = runtimes,
                            initialized = true,
                            error = null
                        )
                    }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (throwable: Throwable) {
                    _uiState.update {
                        it.copy(initialized = true, error = throwable)
                    }
                } finally {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    fun refresh(forceLoad: Boolean = true) {
        refreshRequests.trySend(forceLoad)
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        refreshRequests.close()
    }
}

@Composable
private fun rememberJavaManageViewModel(): JavaManageViewModel = viewModel(
    key = NormalNavKey.Settings.JavaManager.toString()
) {
    JavaManageViewModel()
}

@Composable
fun JavaManageScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val context = LocalContext.current
    val viewModel = rememberJavaManageViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { throwable ->
            submitError(
                ErrorViewModel.ThrowableMessage(
                    title = context.getString(R.string.multirt_runtime_load_failed),
                    message = throwable.getMessageOrToString()
                )
            )
            viewModel.consumeError()
        }
    }

    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.JavaManager, settingsScreenKey, false)
    ) { isVisible ->
        val yOffset = swapAnimateDpAsState(
            targetValue = (-40).dp,
            swapIn = isVisible
        )

        var runtimeOperation by remember { mutableStateOf<RuntimeOperation>(RuntimeOperation.None) }
        RuntimeOperation(
            runtimeOperation = runtimeOperation,
            updateOperation = { runtimeOperation = it },
            callRefresh = { viewModel.refresh() },
            submitError = submitError
        )

        SettingsCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 12.dp)
                .offset { IntOffset(x = 0, y = yOffset.value.roundToPx()) },
            position = CardPosition.Single
        ) {
            CardTitleLayout {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconTextButton(
                        onClick = { viewModel.refresh() },
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = stringResource(R.string.generic_refresh),
                        text = stringResource(R.string.generic_refresh),
                        enabled = !uiState.isRefreshing
                    )
                    ImportMultipleFileButton(
                        extension = "xz",
                        progressUris = { uris ->
                            uris.forEach { uri ->
                                progressRuntimeUri(
                                    context = context,
                                    uri = uri,
                                    callRefresh = { viewModel.refresh() },
                                    submitError = submitError
                                )
                            }
                        }
                    )
                    ImportSingleFileButton(
                        extension = "jar",
                        progressUris = { uris ->
                            uris[0].let { uri ->
                                RuntimesManager.getExactJreName(8) ?: run {
                                    Toast.makeText(context, R.string.multirt_no_java_8, Toast.LENGTH_LONG).show()
                                    return@ImportSingleFileButton
                                }
                                (context as? Activity)?.let { activity ->
                                    val jreName = AllSettings.javaRuntime.takeIf { AllSettings.autoPickJavaRuntime.getValue() }?.getValue()
                                    executeJarWithUri(activity, uri, jreName)
                                }
                            }
                        },
                        painter = painterResource(R.drawable.ic_terminal_outlined),
                        text = stringResource(R.string.execute_jar_title)
                    )
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            if (!uiState.initialized) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            } else if (uiState.runtimes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_terminal_outlined),
                        contentDescription = null,
                        modifier = Modifier.padding(bottom = 12.dp).size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = stringResource(R.string.multirt_runtime_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.multirt_runtime_count, uiState.runtimes.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    items(
                        items = uiState.runtimes,
                        key = { runtime -> "${runtime.name}:${runtime.javaVersion}:${runtime.arch}" }
                    ) { runtime ->
                        JavaRuntimeItem(
                            runtime = runtime,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onDeleteClick = {
                                runtimeOperation = RuntimeOperation.PreDelete(runtime)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RuntimeOperation(
    runtimeOperation: RuntimeOperation,
    updateOperation: (RuntimeOperation) -> Unit,
    callRefresh: () -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    when(runtimeOperation) {
        is RuntimeOperation.None -> {}
        is RuntimeOperation.PreDelete -> {
            val runtime = runtimeOperation.runtime
            SimpleAlertDialog(
                title = stringResource(R.string.generic_warning),
                text = stringResource(R.string.multirt_runtime_delete_message, runtime.name),
                onConfirm = { updateOperation(RuntimeOperation.Delete(runtime)) },
                onDismiss = { updateOperation(RuntimeOperation.None) }
            )
        }
        is RuntimeOperation.Delete -> {
            val failedMessage = stringResource(R.string.multirt_runtime_delete_failed)
            val runtime = runtimeOperation.runtime
            TaskSystem.submitTask(
                Task.runTask(
                    id = runtime.name,
                    dispatcher = Dispatchers.IO,
                    task = { task ->
                        task.updateMessage(R.string.multirt_runtime_deleting, runtime.name)
                        RuntimesManager.removeRuntime(runtime.name)
                    },
                    onError = {
                        submitError(
                            ErrorViewModel.ThrowableMessage(
                                title = failedMessage,
                                message = it.getMessageOrToString()
                            )
                        )
                    },
                    onFinally = callRefresh
                )
            )
            updateOperation(RuntimeOperation.None)
        }
    }
}

private fun progressRuntimeUri(
    context: Context,
    uri: Uri,
    callRefresh: () -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    fun showError(
        title: String = context.getString(R.string.multirt_runtime_import_failed),
        message: String
    ) {
        submitError(
            ErrorViewModel.ThrowableMessage(
                title = title,
                message = message
            )
        )
    }

    val name = context.getFileName(uri) ?: run {
        showError(message = context.getString(R.string.multirt_runtime_import_failed_file_name))
        return
    }
    TaskSystem.submitTask(
        Task.runTask(
            id = name,
            dispatcher = Dispatchers.IO,
            task = { task ->
                name.checkExtensionOrThrow(listOf("xz"))

                val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                    showError(message = context.getString(R.string.multirt_runtime_import_failed_input_stream))
                    return@runTask
                }
                RuntimesManager.installRuntime(
                    nativeLibDir = PathManager.DIR_NATIVE_LIB,
                    inputStream = inputStream,
                    name = name,
                    updateProgress = { textRes, textArg ->
                        task.updateMessage(textRes, *textArg)
                    }
                )
            },
            onError = {
                showError(message = throwableToString(it))
            },
            onFinally = callRefresh,
            onCancel = {
                runCatching {
                    RuntimesManager.removeRuntime(name)
                    callRefresh()
                }.onFailure { t ->
                    showError(
                        title = context.getString(R.string.multirt_runtime_delete_failed),
                        message = t.getMessageOrToString()
                    )
                }
            }
        )
    )
}

@Composable
private fun JavaRuntimeItem(
    runtime: Runtime,
    modifier: Modifier = Modifier,
    color: Color = itemColor(),
    contentColor: Color = onItemColor(),
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_terminal_outlined),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 4.dp),
                tint = if (runtime.isCompatible()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = runtime.name,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!runtime.isCompatible()) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = stringResource(R.string.multirt_runtime_corrupt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (runtime.isProvidedByLauncher) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = stringResource(R.string.multirt_runtime_provided_by_launcher),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    runtime.versionString?.let { versionStr ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = stringResource(R.string.multirt_runtime_version_name, versionStr),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    runtime.javaVersion.takeIf { it != 0 }?.let { javaVersion ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Text(
                                text = stringResource(R.string.multirt_runtime_version_code, javaVersion),
                                style = MaterialTheme.typography.labelSmall,
                                color = contentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    runtime.arch?.let { arch ->
                        val compatible = ZMCLApplication.DEVICE_ARCHITECTURE == Architecture.archAsInt(arch)
                        if (!compatible) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = stringResource(R.string.multirt_runtime_incompatible_arch, arch),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            IconButton(
                enabled = !runtime.isProvidedByLauncher || !runtime.isCompatible(),
                onClick = onDeleteClick
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_delete_filled),
                    contentDescription = stringResource(R.string.generic_delete)
                )
            }
        }
    }
}
