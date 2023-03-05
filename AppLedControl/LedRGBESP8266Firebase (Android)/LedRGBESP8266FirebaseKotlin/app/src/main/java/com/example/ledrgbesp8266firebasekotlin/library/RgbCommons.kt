package com.example.ledrgbesp8266firebasekotlin.library

import kotlin.math.max
import kotlin.math.min

object RgbCommons {
    fun rgb2int(vararg rgba: Float): Int {
        val r = clamp(rgba[0] * 256.0f, 0.0f, 255.0f).toInt()
        val g = clamp(rgba[1] * 256.0f, 0.0f, 255.0f).toInt()
        val b = clamp(rgba[2] * 256.0f, 0.0f, 255.0f).toInt()

        val a = if (rgba.size > 3) clamp(rgba[3] * 256.0f, 0.0f, 255.0f).toInt() else 255

        return a shl 24 or (r shl 16) or (g shl 8) or b
    }

    private fun clamp(d: Float, lo: Float, hi: Float): Float {
        return min(hi, max(lo, d))
    }

    fun int2rgb(argb: Int, rgba: FloatArray): FloatArray {
        rgba[0] = (argb shr 16 and 0x0ff) / 255.0f
        rgba[1] = (argb shr 8 and 0x0ff) / 255.0f
        rgba[2] = (argb and 0x0ff) / 255.0f

        if (rgba.size > 3) {
            rgba[3] = (argb shr 24 and 0x0ff) / 255.0f
        }

        return rgba
    }
}
