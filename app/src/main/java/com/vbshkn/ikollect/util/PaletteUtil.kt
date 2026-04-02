package com.vbshkn.ikollect.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import coil3.Bitmap

object PaletteUtil {
    fun getDominantColor(
        bitmap: Bitmap,
        defaultColor: Color
    ): Color {
        val palette = Palette.from(bitmap).generate()
        return palette.dominantSwatch?.rgb?.let { Color(it) } ?: defaultColor
    }

    fun getVibrantGradient(
        bitmap: Bitmap,
        defaultColors: List<Color>
    ): Brush {
        val palette = Palette.from(bitmap).generate()
        val dominantColor = palette.dominantSwatch?.rgb?.let { Color(it) }
        val vibrantColor = palette.vibrantSwatch?.rgb?.let { Color(it) }

        if (dominantColor == null || vibrantColor == null) {
            return Brush.verticalGradient(colors = defaultColors)
        }
        return Brush.verticalGradient(colors = listOf(dominantColor, vibrantColor))
    }

    fun getSoftGradient(
        bitmap: Bitmap,
        defaultColors: List<Color>
    ): Brush {
        val palette = Palette.from(bitmap).generate()
        val dominantColor = palette.dominantSwatch?.rgb?.let { Color(it) }
        val mutedColor = palette.lightMutedSwatch?.rgb?.let { Color(it) }

        if (dominantColor == null || mutedColor == null) {
            return Brush.verticalGradient(colors = defaultColors)
        }
        return Brush.verticalGradient(colors = listOf(dominantColor, mutedColor))
    }
}