package twocheg.mod.screens.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.renderers.impl.BuiltRectangle
import twocheg.mod.utils.client.hoverCheck
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.fromRGB
import twocheg.mod.utils.math.random


abstract class RenderArea(
    open val parentArea: RenderArea? = null,
) {
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    var show = true
    internal var showFactor: Delta
    internal var zIndex: Float

    init {
        showFactor = Delta({ show }, parentFactor = { parentArea?.showFactor?.get() ?: 1f })
        zIndex = (parentArea?.zIndex ?: 0f) + 3f
    }

    val areas: MutableList<RenderArea> = ArrayList()

    protected open var debugMode: Boolean = false

    private val debugBorderColor: Int = fromRGB(random(0, 255), random(0, 255), random(0, 255), 100)

    open fun render(
        context: DrawContext,
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float?,
        height: Float?,
        mouseX: Double,
        mouseY: Double
    ) {
        this.x = x
        this.y = y
        this.width = width ?: 0f
        this.height = height ?: 0f

        if (debugMode) {
            val rectangle: BuiltRectangle = Builder.rectangle()
                .size(SizeState(this.width, this.height))
                .color(QuadColorState(debugBorderColor))
                .build()
            rectangle.render(matrix, x, y)
        }
    }

    open fun isVisible(): Boolean {
        return show && (parentArea?.isVisible() ?: true)
    }

    open fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        return isHovered(x, y, width, height, mouseX, mouseY)
    }

    fun getTopHoveredArea(mouseX: Double, mouseY: Double): RenderArea? {
        for (i in areas.size - 1 downTo 0) {
            val area = areas[i]
            if (area.isVisible() && area.isHovered(mouseX, mouseY)) {
                return area.getTopHoveredArea(mouseX, mouseY) ?: area
            }
        }
        return null
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (i in areas.size - 1 downTo 0) {
            if (areas[i].mouseClicked(mouseX, mouseY, button)) return true
        }
        return false
    }

    open fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (i in areas.size - 1 downTo 0) {
            if (areas[i].mouseReleased(mouseX, mouseY, button)) return true
        }
        return false
    }

    open fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        for (i in areas.size - 1 downTo 0) {
            if (areas[i].mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true
        }
        return false
    }

    open fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        for (i in areas.size - 1 downTo 0) {
            if (areas[i].mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true
        }
        return false
    }

    open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (i in areas.size - 1 downTo 0) {
            if (areas[i].keyPressed(keyCode, scanCode, modifiers)) return true
        }
        return false
    }

    open fun charTyped(chr: Char, modifiers: Int): Boolean {
        for (i in areas.size - 1 downTo 0) {
            if (areas[i].charTyped(chr, modifiers)) return true
        }
        return false
    }

    inline fun <reified T : RenderArea> findChild(): T? {
        for (area in areas) {
            if (area is T) return area
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValueArea(value: T): ValueArea<T>? {
        for (area in areas) {
            if (area is ValueArea<*>) {
                if (area.value == value) return area as ValueArea<T>
            }
        }
        return null
    }

    companion object {
        val mc: MinecraftClient = MinecraftClient.getInstance()

        const val PADDING = 4.5f

        fun isHovered(x: Float, y: Float, width: Float, height: Float, mouseX: Double, mouseY: Double): Boolean {
            return hoverCheck(x, y, width, height, mouseX, mouseY)
        }
    }
}