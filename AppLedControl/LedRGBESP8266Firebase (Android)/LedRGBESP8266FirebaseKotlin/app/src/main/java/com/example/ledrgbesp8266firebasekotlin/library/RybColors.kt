package com.example.ledrgbesp8266firebasekotlin.library

import kotlin.math.max
import kotlin.math.min

object RybColors {
    private val keyColors = arrayOf(
        floatArrayOf(1.00f, 0.00f, 0.00f), // * Red
        floatArrayOf(1.00f, 0.00f, 0.50f), // * Magenta
        floatArrayOf(1.00f, 0.00f, 1.00f), // * Purple
        floatArrayOf(0.50f, 0.00f, 1.00f), // * Violet
        floatArrayOf(0.00f, 0.00f, 1.00f), // * Blue
        floatArrayOf(0.00f, 1.00f, 1.00f), // * Aqua
        floatArrayOf(0.00f, 1.00f, 0.00f), // * Green
        floatArrayOf(0.50f, 1.00f, 0.00f), // * Chartreuse
        floatArrayOf(1.00f, 1.00f, 0.00f), // * Yellow
        floatArrayOf(1.00f, 0.75f, 0.00f), // * Amber
        floatArrayOf(1.00f, 0.50f, 0.00f), // * Orange
        floatArrayOf(1.00f, 0.25f, 0.00f) // * Vermilion
    )

    fun colorSegmentsCount(): Int {
        return keyColors.size
    }

    fun red(hue: Float): Float {
        val i0 = (hue * keyColors.size).toInt()
        val fraction = hue * keyColors.size - i0

        val i1 = (i0 + 1) % keyColors.size

        return keyColors[i0][0] * (1 - fraction) + keyColors[i1][0] * fraction
    }

    fun green(hue: Float): Float {
        val i0 = (hue * keyColors.size).toInt()
        val fraction = hue * keyColors.size - i0

        val i1 = (i0 + 1) % keyColors.size

        return keyColors[i0][1] * (1 - fraction) + keyColors[i1][1] * fraction
    }

    fun blue(hue: Float): Float {
        val i0 = (hue * keyColors.size).toInt()
        val fraction = hue * keyColors.size - i0

        val i1 = (i0 + 1) % keyColors.size

        return keyColors[i0][2] * (1 - fraction) + keyColors[i1][2] * fraction
    }

    fun color(hue: Float): Int {
        var i0 = (hue * keyColors.size).toInt()
        val fraction = hue * keyColors.size - i0

        i0 %= keyColors.size
        val i1 = (i0 + 1) % keyColors.size

        return RgbCommons.rgb2int(
            keyColors[i0][0] * (1 - fraction) + keyColors[i1][0] * fraction,
            keyColors[i0][1] * (1 - fraction) + keyColors[i1][1] * fraction,
            keyColors[i0][2] * (1 - fraction) + keyColors[i1][2] * fraction
        )
    }

    fun hue(r0: Float, g0: Float, b0: Float): Float {
        var r = r0
        var g = g0
        var b = b0
        val max = max(r, max(g, b))
        val min = min(r, min(g, b))

        if (max - min == 0f) {
            return 0f
        }

        r = (r - min) / (max - min)
        g = (g - min) / (max - min)
        b = (b - min) / (max - min)

        assert((r == 1f || g == 1f || b == 1f) && (r == 0f || g == 0f || b == 0f))

        var i0 = 0
        var i1: Int

        while (true) {
            i1 = (i0 + 1) % keyColors.size

            if (inRange(r, keyColors[i0][0], keyColors[i1][0]) && inRange(
                    g,
                    keyColors[i0][1],
                    keyColors[i1][1]
                ) && inRange(b, keyColors[i0][2], keyColors[i1][2])
            ) {
                break
            }

            if (++i0 >= keyColors.size) {
                throw IllegalArgumentException()
            }
        }

        val fraction = max(
            max(
                rangeFraction(r, keyColors[i0][0], keyColors[i1][0]),
                rangeFraction(g, keyColors[i0][1], keyColors[i1][1])
            ),
            rangeFraction(b, keyColors[i0][2], keyColors[i1][2])
        )

        return (i0 + fraction) / keyColors.size
    }

    private fun inRange(x: Float, a: Float, b: Float): Boolean {
        if (b < a) {
            return x in b..a
        }

        return x in a..b
    }

    private fun rangeFraction(x: Float, a: Float, b: Float): Float {
        return if (b - a == 0f) 0f else (x - a) / (b - a)
    }

}
