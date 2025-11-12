package twocheg.mod.utils.math

import java.awt.*
import kotlin.math.*

fun fromRGB(r: Int, g: Int, b: Int, a: Int): Int {
    var r = r
    var g = g
    var b = b
    if (a !in 5..255) {
        return 0x00000000
    }
    r = Math.clamp(r.toLong(), 0, 255)
    g = Math.clamp(g.toLong(), 0, 255)
    b = Math.clamp(b.toLong(), 0, 255)
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}

fun fromRGB(r: Int, g: Int, b: Int, a: Float): Int {
    return fromRGB(r, g, b, a.toInt())
}

fun fromRGB(r: Int, g: Int, b: Int): Int {
    return fromRGB(r, g, b, 255)
}

fun fromInt(color: Int): Color {
    val red = (color shr 16) and 0xFF
    val green = (color shr 8) and 0xFF
    val blue = color and 0xFF
    val alpha = (color shr 24) and 0xff
    return Color(red, green, blue, alpha)
}

fun applyOpacity(color: Color, opacity: Float): Color {
    var opacity = opacity
    opacity = min(1f, max(0f, opacity))
    return Color(color.red, color.green, color.blue, (color.alpha * opacity).toInt())
}

fun applyOpacity(colorInt: Int, opacity: Float): Int {
    var opacity = opacity
    opacity = min(1f, max(0f, opacity))
    val color = Color(colorInt)
    return Color(color.red, color.green, color.blue, (color.alpha * opacity).toInt()).rgb
}

fun hasAlpha(color: Int): Boolean {
    return ((color shr 24) and 0xFF) < 255
}

fun reverseColor(c: Color): Color {
    return Color(255 - c.red, 255 - c.green, 255 - c.blue, c.alpha)
}

fun lerpColor(start: Color, end: Color, delta: Float): Color {
    val r = start.red + (end.red - start.red) * delta
    val g = start.green + (end.green - start.green) * delta
    val b = start.blue + (end.blue - start.blue) * delta
    val a = start.alpha + (end.alpha - start.alpha) * delta
    return Color(
        r.toInt().coerceIn(0, 255),
        g.toInt().coerceIn(0, 255),
        b.toInt().coerceIn(0, 255),
        a.toInt().coerceIn(0, 255)
    )
}

fun lerpColor(startArgb: Int, endArgb: Int, delta: Float): Int {
    val startA = (startArgb ushr 24) and 0xFF
    val startR = (startArgb ushr 16) and 0xFF
    val startG = (startArgb ushr 8) and 0xFF
    val startB = startArgb and 0xFF

    val endA = (endArgb ushr 24) and 0xFF
    val endR = (endArgb ushr 16) and 0xFF
    val endG = (endArgb ushr 8) and 0xFF
    val endB = endArgb and 0xFF

    val a = startA + (endA - startA) * delta
    val r = startR + (endR - startR) * delta
    val g = startG + (endG - startG) * delta
    val b = startB + (endB - startB) * delta

    return (
        (a.toInt().coerceIn(0, 255) shl 24) or
        (r.toInt().coerceIn(0, 255) shl 16) or
        (g.toInt().coerceIn(0, 255) shl 8) or
        b.toInt().coerceIn(0, 255)
    )
}