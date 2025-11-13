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

class BooleanArea(
    override val parentArea: RenderArea,
    boolSet: Setting<Boolean>
) : SettingArea<Setting<Boolean>>(parentArea, boolSet) {
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

        val color = fromRGB(130, 130, 130 + (100 * enableFactor.get()).toInt(), 255 * showFactor.get())
        val rectangle = Builder.rectangle()
            .size(SizeState(BTN_WIDTH - PADDING * 2, 3f))
            .color(QuadColorState(color))
            .radius(QuadRadiusState(1f))
            .build()
        val rectX = x + width - BTN_WIDTH
        val rectY = y + (this.height / 2 - BTH_HEIGHT / 2)
        rectangle.render(matrix, rectX + PADDING, rectY + (BTH_HEIGHT / 2 - 1.5f), zIndex)

        val centerRect =  Builder.rectangle()
            .size(SizeState(BTH_HEIGHT, BTH_HEIGHT))
            .color(rectangle.color)
            .radius(QuadRadiusState((BTH_HEIGHT / 2)-1))
            .build()
        centerRect.render(matrix, rectX + (BTN_WIDTH / 2) * enableFactor.get(), rectY)

        super.render(context, matrix, x, y, width, this.height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(mouseX, mouseY)) setting.setValue(!setting.getValue())
        return super.mouseClicked(mouseX, mouseY, button)
    }
}