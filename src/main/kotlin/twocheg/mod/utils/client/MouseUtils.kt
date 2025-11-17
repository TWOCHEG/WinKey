package twocheg.mod.utils.client

import twocheg.mod.modules.Parent

object MouseUtils {
    fun hoverCheck(x: Float, y: Float, width: Float, height: Float, mouseX: Double, mouseY: Double): Boolean {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height)
    }

    fun getPos(): DoubleArray {
        val s = Parent.mc.window.scaleFactor
        val mouseX = Parent.mc.mouse.x / s
        val mouseY = Parent.mc.mouse.y / s
        return doubleArrayOf(mouseX, mouseY)
    }
}