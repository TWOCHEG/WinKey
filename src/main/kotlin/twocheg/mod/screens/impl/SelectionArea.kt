package twocheg.mod.screens.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.utils.math.Lerp
import twocheg.mod.utils.math.fromRGB

class SelectionArea(
    override val parentArea: RenderArea,
    val targetArea: () -> RenderArea? = { null }
) : RenderArea(parentArea) {
    val targetX = Lerp(0f)
    val targetY = Lerp(0f)
    val targetWidth = Lerp(0f)
    val targetHeight = Lerp(0f)

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
        var x = x
        var y = y
        var width = width
        var height = height

        val targetArea = targetArea()
        if ((width == null || height == null) && targetArea != null) {
            x = targetArea.x
            y = targetArea.y
            width = targetArea.width
            height = targetArea.height
        }

        if (targetX.get() == 0f && targetY.get() == 0f && targetWidth.get() == 0f && targetHeight.get() == 0f) {
            targetX.forceSet(x)
            targetY.forceSet(y)
            targetWidth.forceSet(width!!)
            targetHeight.forceSet(height!!)
        } else {
            targetX.set(x)
            targetY.set(y)
            targetWidth.set(width!!)
            targetHeight.set(height!!)
        }

        val rectangle = Builder.rectangle()
            .size(SizeState(targetWidth.get(), targetHeight.get()))
            .color(QuadColorState(fromRGB(0, 0, 0, 50 * showFactor.get())))
            .radius(QuadRadiusState(6f))
            .build()
        rectangle.render(matrix, targetX.get(), targetY.get(), zIndex - 2)
        Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 18 * showFactor.get())))
            .radius(rectangle.radius)
            .thickness(0.1f)
            .build()
            .render(matrix, targetX.get(), targetY.get(), zIndex - 2)

        super.render(context, matrix, x, y, width, height, mouseX, mouseY)
    }
}