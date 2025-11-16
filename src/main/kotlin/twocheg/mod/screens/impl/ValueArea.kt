package twocheg.mod.screens.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.fromRGB
import java.util.function.Consumer
import java.util.function.Supplier


class ValueArea<T>(
    override val parentArea: RenderArea?,
    var value: T,
    val task: Consumer<T>,
    val currentValue: Supplier<T>, // для анимаций
    val valueName: String,
) : RenderArea(parentArea) {
    val alpha = Delta({ currentValue.get() == value })

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
        val text: BuiltText = Builder.text()
            .font(bikoFont.get())
            .text(valueName)
            .color(fromRGB(
                255, 255, 255,
                (155 + (100 * alpha.get())) * showFactor.get()
            ))
            .size(14f)
            .thickness(0.05f)
            .build()

        this.width = text.width + PADDING * 2
        this.height = text.size + PADDING * 2

//        if (drawBg) {
//            Builder.rectangle()
//                .size(SizeState(this.width, this.height))
//                .color(QuadColorState(fromRGB(0, 0, 0, 50 * showFactor.get())))
//                .radius(QuadRadiusState(6f))
//                .build()
//                .render(matrix, x, y, 1f)
//        }

        text.render(matrix, x + PADDING, y + PADDING, zIndex + 1)

        super.render(context, matrix, x, y, this.width, this.height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(mouseX, mouseY) && value != currentValue.get()) {
            task.accept(value)
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}