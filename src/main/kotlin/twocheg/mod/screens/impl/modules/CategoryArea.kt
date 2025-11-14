package twocheg.mod.screens.impl.modules

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.Categories
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.moduleManager
import twocheg.mod.modules.Parent
import twocheg.mod.modules.client.ClickGui
import twocheg.mod.renderers.impl.BuiltBlur
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.utils.math.AnimType
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.Lerp
import twocheg.mod.utils.math.fromRGB

class CategoryArea(
    val category: Categories,
    val modules: List<Parent>
) : RenderArea() {
    val targetHeight = Lerp(0f)

    companion object {
        const val MODULE_PADDING = 5f
        const val MODULE_HEIGHT = 25f
    }

    fun reset() {
        show = false
        showFactor.setProgress(0f)
    }

    init {
        showFactor = Delta({ show }, mode = AnimType.EaseOut)
        for (module in modules) areas.add(ModuleArea(module, this))
    }

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
        this.y = y - 100 * (1 - showFactor.get())

        val text: BuiltText = Builder.text()
            .font(bikoFont.get())
            .text(category.name)
            .color(fromRGB(255, 255, 255, 200 * showFactor.get()))
            .size(14f)
            .thickness(0.05f)
            .build()
        val rectangle = Builder.rectangle()
            .size(SizeState(text.width + PADDING * 3, text.size + PADDING * 2))
            .color(QuadColorState(fromRGB(0, 0, 0, 40 * showFactor.get())))
            .radius(QuadRadiusState(8f))
            .build()

        val blur: BuiltBlur = Builder.blur()
            .size(SizeState(width!!, targetHeight.get()))
            .radius(QuadRadiusState(10f))
            .blurRadius(12f)
            .color(
                QuadColorState(
                    fromRGB(110, 110, 110, 255 * showFactor.get())
                )
            )
            .build()
        blur.render(matrix, x, this.y)

        val mainRectBorder = Builder.border()
            .size(blur.size)
            .color(
                QuadColorState(
                    fromRGB(255, 255, 255, 25 * showFactor.get())
                )
            )
            .thickness(0.2f)
            .radius(blur.radius)
            .build()
        mainRectBorder.render(matrix, x, this.y)

        var renderY = this.y + 8f

        val rectX = x + (width / 2f - rectangle.size.width / 2f)

        // заголовок
        rectangle.render(matrix, rectX, renderY)
        text.render(matrix, rectX + PADDING, renderY + PADDING)
        Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 10 * showFactor.get())))
            .radius(rectangle.radius)
            .thickness(0.1f)
            .build()
            .render(matrix, rectX, renderY)

        renderY += rectangle.size.height + 5f

        // разделитель
        val separator = Builder.rectangle()
            .size(SizeState(width - 40f, 2f))
            .color(mainRectBorder.color)
            .radius(QuadRadiusState(1f))
            .build()
        separator.render(matrix, x + width / 2 - separator.size.width / 2, renderY)

        renderY += separator.size.height

        renderY += 5f

        val gui = moduleManager.get(ClickGui::class.java)!!

        for (area in areas) {
            if (gui.selectScreens.isSearch) {
                val q = (gui.selectScreens.areas.last() as ModuleSearchArea).q
                if (!(area as ModuleArea).module.name.startsWith(q)) continue
            }
            area.render(context, matrix, x + PADDING, renderY, width - PADDING * 2f, MODULE_HEIGHT, mouseX, mouseY)
            renderY += area.totalHeight + MODULE_PADDING
        }

        if (targetHeight.get() == 0f) targetHeight.forceSet(renderY - this.y)
        targetHeight.set(renderY - this.y)

        super.render(context, matrix, x, this.y, width, targetHeight.get(), mouseX, mouseY)
    }
}