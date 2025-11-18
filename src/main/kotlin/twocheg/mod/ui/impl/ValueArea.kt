package twocheg.mod.ui.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.api.render.ContainerArea
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.utils.math.ColorUtils.fromRGB
import twocheg.mod.utils.math.Delta

class ValueArea<T>(
    val value: T,
    val valueName: String,
    val onSet: (T) -> Unit,
    val current: () -> T
) : ContainerArea() {
    private val _highlighting = Delta({ value == current() })

    var highlighting: Float
        get() = _highlighting.get()
        set(value) = _highlighting.setProgress(value)

    val fontSize = 14f

    override fun render(
        context: DrawContext,
        matrices: Matrix4f,
        mouseX: Double,
        mouseY: Double
    ) {
        val bikoFont = bikoFont()
        val textWidth = bikoFont.getWidth(valueName, fontSize)

        if (width == 0f) width = textWidth + PADDING * 2
        if (height == 0f) height = fontSize + PADDING * 2

        val text = Builder.text()
            .font(bikoFont())
            .text(valueName)
            .color(fromRGB(
                255, 255, 255,
                (155 + (100 * highlighting)) * showFactor
            ))
            .size(fontSize)
            .thickness(0.05f)
            .build()

        text.render(matrices, x + (width / 2 - textWidth / 2), y + (height / 2 - fontSize / 2))
    }

    override fun recalculateLayout(availableWidth: Float, availableHeight: Float) {
        if (availableWidth != 0f) width = availableWidth
        if (availableHeight != 0f) height = availableHeight
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(mouseX, mouseY) && value != current()) onSet(value)
        return super.mouseClicked(mouseX, mouseY, button)
    }
}