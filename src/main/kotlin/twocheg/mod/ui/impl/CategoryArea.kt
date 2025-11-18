package twocheg.mod.ui.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.Categories
import twocheg.mod.api.modules.Module
import twocheg.mod.api.render.VerticalContainerArea
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.utils.math.ColorUtils.fromRGB
import twocheg.mod.utils.math.Spring

class CategoryArea(
    val data: Pair<Categories, List<Module>>,
    width: Float
) : VerticalContainerArea(width) {
    val targetHeight = Spring(0f)

    override fun render(
        context: DrawContext,
        matrices: Matrix4f,
        mouseX: Double,
        mouseY: Double
    ) {
        val y = y - 100 * (1 - showFactor)

        val blur = Builder.blur()
            .size(SizeState(width, targetHeight.get()))
            .radius(QuadRadiusState(10f))
            .blurRadius(12f)
            .color(
                QuadColorState(
                    fromRGB(110, 110, 110, 255 * showFactor)
                )
            )
            .build()
        blur.render(matrices, this.x, y)

        Builder.border()
            .size(blur.size)
            .color(
                QuadColorState(
                    fromRGB(255, 255, 255, 25 * showFactor)
                )
            )
            .thickness(0.2f)
            .radius(blur.radius)
            .build()
            .render(matrices, this.x, y)
    }

    override fun recalculateLayout(availableWidth: Float, availableHeight: Float) {
        height = 100f
        if (targetHeight.get() == 0f) targetHeight.forceSet(height)
        else targetHeight.set(height)
    }
}