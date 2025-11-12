package twocheg.mod.screens.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.modules.Parent
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.fromRGB

class ModuleArea(
    val module: Parent,
    override val parentArea: RenderArea,
    override val zIndex: Float
) : RenderArea(parentArea, zIndex) {
    val enableFactor = Delta({ module.enable })

    var expanded = false
    val expandedFactor = Delta({ expanded })

    override fun render(
        context: DrawContext,
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float?,
        height: Float?,
        mouseX: Double,
        mouseY: Double
    ) {
        val settingsHeight = 20f * expandedFactor.get()
        this.height = height!! + settingsHeight

        val c = 0 + (50 * (1 - expandedFactor.get()) * enableFactor.get()).toInt()
        val rectangle = Builder.rectangle()
            .size(SizeState(width!!, this.height))
            .radius(QuadRadiusState(6f))
            .color(QuadColorState(fromRGB(c, c, c, 40 * showFactor.get())))
            .build()
        rectangle.render(matrix, x, y)
        val border = Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 25 * showFactor.get())))
            .radius(rectangle.radius)
            .thickness(0.2f)
            .build()
        border.render(matrix, x, y, 1f)

        val text: BuiltText = Builder.text()
            .font(bikoFont.get())
            .text(module.name)
            .color(fromRGB(255, 255, 255, 255 * showFactor.get()))
            .size(14f)
            .thickness(0.05f)
            .build()
        val textX = x + (width / 2 - text.width / 2)
        val textY = y + (height / 2 - text.size / 2)
        text.render(matrix, textX, textY)

        val borderLineWidth = text.width
        Builder.rectangle()
            .size(SizeState(borderLineWidth * enableFactor.get(), 2f))
            .radius(QuadRadiusState(1f))
            .color(QuadColorState(fromRGB(255, 255, 255, 255 * showFactor.get())))
            .build()
            .render(
                matrix,
                textX + (if (!module.enable) borderLineWidth * (1 - enableFactor.get()) else 0).toFloat(),
                textY + text.size - 3f
            )

        context.enableScissor(
            (x + 2).toInt(),
            (y + 2).toInt(),
            (x + width - 4).toInt(),
            (y + this.height - 4).toInt()
        )

        if (expandedFactor.get() != 0f) {
            // TODO рендеринг настроек
        }

        context.disableScissor()

        super.render(context, matrix, x, y, width, this.height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(x, y, width, CategoryArea.MODULE_HEIGHT, mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) module.toggle()
            else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) expanded = !expanded
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}