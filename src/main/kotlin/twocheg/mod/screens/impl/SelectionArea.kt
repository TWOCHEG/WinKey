package twocheg.mod.screens.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.utils.math.Spring
import twocheg.mod.utils.math.fromRGB

class SelectionArea(
    override val parentArea: RenderArea,
    val durationMs: Long = 400
) : RenderArea(parentArea) {
    var targetArea: () -> RenderArea? = { null }

    val targetX = Spring(0f)
    val targetY = Spring(0f)
    val targetWidth = Spring(0f)
    val targetHeight = Spring(0f)

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
        var areaX = x
        var areaY = y
        var width = width
        var height = height

        // предполагается то что рендер будет использоваться так, что величины никогда не будут null
        if (targetArea() != null && (width == null && height == null)) {
            areaX = targetArea()!!.x
            areaY = targetArea()!!.y
            width = targetArea()!!.width
            height = targetArea()!!.height
        }

        targetX.set(areaX - x)
        targetY.set(areaY - y)
        targetWidth.set(width!!)
        targetHeight.set(height!!)

        val x = x + targetX.get()
        val y = y + targetY.get()

        val rectangle = Builder.rectangle()
            .size(SizeState(targetWidth.get(), targetHeight.get()))
            .color(QuadColorState(fromRGB(0, 0, 0, 50 * showFactor.get())))
            .radius(QuadRadiusState(6f))
            .build()
        rectangle.render(matrix, x, y, zIndex - 2)
        Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 18 * showFactor.get())))
            .radius(rectangle.radius)
            .thickness(0.1f)
            .build()
            .render(matrix, x, y, zIndex - 2)

        super.render(context, matrix, x, y, width, height, mouseX, mouseY)
    }
}