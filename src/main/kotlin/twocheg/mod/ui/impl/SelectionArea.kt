package twocheg.mod.ui.impl

import com.google.common.base.Supplier
import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.api.render.ContainerArea
import twocheg.mod.api.render.RenderArea
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.utils.math.ColorUtils.fromRGB
import twocheg.mod.utils.math.Spring

class SelectionArea() : ContainerArea() {
    var targetArea: () -> RenderArea? = { null }

    private val targetX = Spring(0f)
    private val targetY = Spring(0f)
    private val targetWidth = Spring(0f)
    private val targetHeight = Spring(0f)

    override fun render(
        context: DrawContext,
        matrices: Matrix4f,
        mouseX: Double,
        mouseY: Double
    ) {
        if (targetX.target == 0f) targetX.forceSet(x)
        else targetX.set(x)
        if (targetY.target == 0f) targetY.forceSet(y)
        else targetY.set(y)
        if (targetWidth.target == 0f) targetWidth.forceSet(width)
        else targetWidth.set(width)
        if (targetHeight.target == 0f) targetHeight.forceSet(height)
        else targetHeight.set(height)

        val rectangle = Builder.rectangle()
            .size(SizeState(targetWidth.get(), targetHeight.get()))
            .color(QuadColorState(fromRGB(0, 0, 0, 50 * showFactor)))
            .radius(QuadRadiusState(6f))
            .build()
        rectangle.render(matrices, targetX.get(), targetY.get())
        Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 18 * showFactor)))
            .radius(rectangle.radius)
            .thickness(0.1f)
            .build()
            .render(matrices, targetX.get(), targetY.get())
    }

    override fun recalculateLayout(availableWidth: Float, availableHeight: Float) {
        val targetArea = targetArea()
        if (targetArea != null) {
            x = targetArea.x
            y = targetArea.y
            width = targetArea.width
            height = targetArea.height
        }
    }
}