/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.zhangyuhexht.zmcl.setting.enums

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.zhangyuhexht.zmcl.R

enum class AppLanguage(
    val tag: String,
    @param:StringRes val textRes: Int
) {
    ENGLISH_US("en-US", R.string.language_english_us),
    ENGLISH_UK("en-GB", R.string.language_english_uk),
    SIMPLIFIED_CHINESE("zh-CN", R.string.language_simplified_chinese),
    TRADITIONAL_CHINESE("zh-TW", R.string.language_traditional_chinese)
}

fun applyLanguage(language: AppLanguage) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(language.tag)
    )
}
