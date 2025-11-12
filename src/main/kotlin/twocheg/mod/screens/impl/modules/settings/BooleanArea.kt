package twocheg.mod.screens.impl.modules.settings

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.settings.Setting
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.fromRGB
import twocheg.mod.utils.math.splitText

class BooleanArea(override val parentArea: RenderArea, val setting: Setting<Boolean>) : RenderArea(parentArea) {
    companion object {
        const val BTN_WIDTH = 30f
        const val BTH_HEIGHT = 14f
    }

    val enableFactor = Delta({ setting.getValue() })

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
        val bikoFont = bikoFont.get()

        val lines = splitText(
            setting.name,
            width!! - BTN_WIDTH - PADDING
        ) { text -> bikoFont.getWidth(text, 14f) * BuiltText.widthFactor }
        var renderY = y
        for (line in lines) {
            val text: BuiltText = Builder.text()
                .font(bikoFont)
                .text(line)
                .color(fromRGB(255, 255, 255, 200 * showFactor.get()))
                .size(14f)
                .thickness(0.05f)
                .build()
            text.render(matrix, x, renderY, zIndex)
            renderY += text.size + 2f
        }
        renderY -= 2f

        this.height = renderY - y

        val rectangle = Builder.rectangle()
            .size(SizeState(BTN_WIDTH, BTH_HEIGHT))
            .color(QuadColorState(
                fromRGB(100, 100, 100 + (100 * enableFactor.get()).toInt(), 100 * showFactor.get())
            ))
            .radius(QuadRadiusState(4f))
            .build()
        val rectX = x + width - BTN_WIDTH
        val rectY = y + (this.height / 2 - BTH_HEIGHT / 2)
        rectangle.render(matrix, rectX, rectY, zIndex)

        val centerRect =  Builder.rectangle()
            .size(SizeState((BTN_WIDTH / 2) - 2, BTH_HEIGHT - 2))
            .color(QuadColorState(
                fromRGB(255, 255, 255, 30 * showFactor.get())
            ))
            .radius(QuadRadiusState(3f))
            .build()
        centerRect.render(matrix, rectX + 1 + (BTN_WIDTH / 2) * enableFactor.get(), rectY + 1)

        Builder.border()
            .size(rectangle.size)
            .color(centerRect.color)
            .thickness(0.1f)
            .radius(rectangle.radius)
            .build()
            .render(matrix, rectX, rectY, zIndex + 2)

        super.render(context, matrix, x, y, width, this.height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(mouseX, mouseY)) setting.setValue(!setting.getValue())
        return super.mouseClicked(mouseX, mouseY, button)
    }
}