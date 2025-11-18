package twocheg.mod.api.render

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f

interface RenderArea {
    var x: Float
    var y: Float
    var width: Float
    var height: Float

    var parent: RenderArea?

    fun render(context: DrawContext, matrices: Matrix4f, mouseX: Double, mouseY: Double)

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean = false
    fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean = false
    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false
    fun charTyped(chr: Char, modifiers: Int): Boolean = false

    fun isHovered(mouseX: Double, mouseY: Double): Boolean =
        mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height

    fun recalculateLayout(availableWidth: Float, availableHeight: Float)

    val show: Boolean
    val showFactor: Float
}