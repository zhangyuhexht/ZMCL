/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.zhangyuhexht.zmcl.setting.AllSettings
import com.zhangyuhexht.zmcl.setting.enums.LauncherIcon
import com.zhangyuhexht.zmcl.utils.logging.Logger

object LauncherIconManager {
    private const val TAG = "LauncherIcon"

    fun select(context: Context, icon: LauncherIcon): Boolean {
        return apply(context, icon).also { success ->
            if (success) AllSettings.launcherIcon.save(icon)
        }
    }

    fun sync(context: Context) {
        apply(context, AllSettings.launcherIcon.getValue())
    }

    fun apply(context: Context, icon: LauncherIcon): Boolean {
        val packageManager = context.packageManager
        return runCatching {
            val changes = LauncherIcon.entries.mapNotNull { entry ->
                val enabled = entry == icon
                val component = ComponentName(context.packageName, entry.aliasClassName)
                val currentState = packageManager.getComponentEnabledSetting(component)
                val currentlyEnabled = when (currentState) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> entry.enabledByDefault
                    else -> false
                }
                if (currentlyEnabled == enabled) null else Triple(entry, component, enabled)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.setComponentEnabledSettings(
                    changes.map { (_, component, enabled) ->
                        PackageManager.ComponentEnabledSetting(
                            component,
                            if (enabled) {
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            } else {
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            },
                            PackageManager.DONT_KILL_APP
                        )
                    }
                )
            } else {
                changes
                    .sortedByDescending { (_, _, enabled) -> enabled }
                    .forEach { (entry, _, enabled) ->
                        setAliasState(packageManager, context, entry, enabled)
                    }
            }
        }.onFailure { error ->
            Logger.error(TAG, "Failed to switch launcher icon to ${icon.name}", error)
        }.isSuccess
    }

    private fun setAliasState(
        packageManager: PackageManager,
        context: Context,
        icon: LauncherIcon,
        enabled: Boolean
    ) {
        val component = ComponentName(context.packageName, icon.aliasClassName)
        val currentState = packageManager.getComponentEnabledSetting(component)
        val currentlyEnabled = when (currentState) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> icon.enabledByDefault
            else -> false
        }
        if (currentlyEnabled == enabled) return

        packageManager.setComponentEnabledSetting(
            component,
            if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            PackageManager.DONT_KILL_APP
        )
    }
}
