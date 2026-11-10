/*
 * ZMCL
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 */

package com.zhangyuhexht.zmcl.utils.animation

import com.zhangyuhexht.zmcl.R

/** Global motion preference shared by navigation and onboarding. */
enum class MotionLevel(
    val textRes: Int,
    val summaryRes: Int,
    val durationMillis: Int,
    val slideDivisor: Int,
    val staggerScale: Float,
) {
    FULL(R.string.motion_level_full, R.string.motion_level_full_summary, 240, 36, 0.72f),
    STANDARD(R.string.motion_level_standard, R.string.motion_level_standard_summary, 180, 56, 0.45f),
    REDUCED(R.string.motion_level_reduced, R.string.motion_level_reduced_summary, 120, 80, 0.16f),
    OFF(R.string.motion_level_off, R.string.motion_level_off_summary, 0, Int.MAX_VALUE, 0f),
}
