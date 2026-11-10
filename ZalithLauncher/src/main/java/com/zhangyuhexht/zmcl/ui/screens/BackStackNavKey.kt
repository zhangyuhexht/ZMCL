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

package com.zhangyuhexht.zmcl.ui.screens

import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavBackStack
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
abstract class BackStackNavKey<E: TitledNavKey>(
    @field:StringRes
    override val title: Int? = null
) : TitledNavKey {
    /** 当前屏幕正在使用的导航栈。 */
    @Contextual
    val backStack: NavBackStack<E> = NavBackStack()

    /**
     * 始终直接读取栈顶，避免先更新导航栈、下一帧才同步当前页面造成的点击停顿。
     * 保留 setter 仅用于兼容旧调用点；导航状态只以 [backStack] 为准。
     */
    var currentKey: E?
        get() = backStack.lastOrNull()
        set(@Suppress("UNUSED_PARAMETER") value) = Unit

    @Suppress("unused")
    fun navigateOnce(key: E) {
        backStack.navigateOnce(key)
    }

    fun navigateTo(screenKey: E, useClassEquality: Boolean = false) {
        backStack.navigateTo(screenKey, useClassEquality)
    }

    fun removeAndNavigateTo(remove: KClass<*>, screenKey: E, useClassEquality: Boolean = false) {
        backStack.removeAndNavigateTo(remove, screenKey, useClassEquality)
    }

    fun removeAndNavigateTo(removes: List<KClass<*>>, screenKey: E, useClassEquality: Boolean = false) {
        backStack.removeAndNavigateTo(removes, screenKey, useClassEquality)
    }

    fun clearWith(navKey: E) {
        backStack.clearWith(navKey)
    }
}
