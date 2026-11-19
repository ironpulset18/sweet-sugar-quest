package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.ui.graphics.Color
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.domain.model.Bloom

fun spriteResFor(slot: Int): Int = when (slot) {
    1 -> R.drawable.sprite_1
    2 -> R.drawable.sprite_2
    3 -> R.drawable.sprite_3
    4 -> R.drawable.sprite_4
    else -> R.drawable.sprite_5
}

fun spriteResFor(bloom: Bloom): Int = spriteResFor(bloom.spriteSlot)

fun bloomColor(bloom: Bloom): Color = Color(bloom.argb.toInt())
