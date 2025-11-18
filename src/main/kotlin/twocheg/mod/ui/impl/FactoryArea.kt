package twocheg.mod.ui.impl

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import org.joml.Matrix4f
import twocheg.mod.api.render.HorizontalContainerArea
import twocheg.mod.api.render.RenderArea
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.ui.ScreensFactory
import twocheg.mod.utils.math.ColorUtils.fromRGB


class FactoryArea(
    val factory: ScreensFactory<*>
) : HorizontalContainerArea(0f) {
    init {
        @Suppress("MUTABLE_PROPERTY_WITH_CAPTURED_TYPE")
        for (screen in factory.classList) {
            children.add(
                ValueArea<Class<out Screen>>(
                    screen, screen.simpleName,
                    factory::setAny,
                    factory::currentClass
                ).apply {
                    parent = this@FactoryArea
                }
            )
        }
    }

    val selection = SelectionArea()

    override fun render(
        context: DrawContext,
        matrices: Matrix4f,
        mouseX: Double,
        mouseY: Double
    ) {
        val blur = Builder.blur()
            .size(SizeState(width, height))
            .radius(QuadRadiusState(6f))
            .blurRadius(12f)
            .color(QuadColorState(
                fromRGB(110, 110, 110, 255 * showFactor)
            ))
            .build()
        blur.render(matrices, x, y)
        Builder.border()
            .size(blur.size)
            .color(QuadColorState(
                fromRGB(255, 255, 255, 10 * showFactor)
            ))
            .radius(QuadRadiusState(6f))
            .thickness(0.2f)
            .build()
            .render(matrices, x, y)

        selection.apply {
            targetArea = { findChild(factory.currentClass) }
            recalculateLayout(0f, 0f)
            render(context, matrices, mouseX, mouseY)
        }

        var currentX = x + PADDING
        for (child in children) {
            child.apply {
                x = currentX
                y = this@FactoryArea.y + PADDING
                render(context, matrices, mouseX, mouseY)
            }
            currentX += child.width + PADDING
        }
    }

    override fun recalculateLayout(availableWidth: Float, availableHeight: Float) {
        width = PADDING
        for (child in children) {
            child.recalculateLayout(0f, availableHeight - PADDING * 2)
            width += child.width + PADDING
        }
        height = availableHeight
    }
}