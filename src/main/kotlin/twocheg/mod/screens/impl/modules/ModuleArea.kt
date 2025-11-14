package twocheg.mod.screens.impl.modules

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.modules.Parent
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.screens.impl.modules.settings.BooleanArea
import twocheg.mod.screens.impl.modules.settings.ListArea
import twocheg.mod.screens.impl.modules.settings.SettingArea
import twocheg.mod.settings.Setting
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.fromRGB

class ModuleArea(
    val module: Parent,
    override val parentArea: RenderArea,
) : RenderArea(parentArea) {
    val enableFactor = Delta({ module.enable })

    var expanded = false
    val expandedFactor = Delta({ expanded })

    init {
        areas += putToAreas(module.settings, this)

        for (area in areas) {
            val area = area as SettingArea<*>
            area.showFactor = Delta({ area.show }, parentFactor = { expandedFactor.get() * area.visibleFactor.get() * showFactor.get() })
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun putToAreas(settings: List<Setting<*>>, parentArea: RenderArea): List<RenderArea> {
        val areas = mutableListOf<RenderArea>()
        // TODO когда будут области групп доделать
        for (setting in settings) {
            if (parentArea !is ModuleArea && setting.parentGroup != null) continue
            if (setting.isBoolean) areas.add(BooleanArea(parentArea, setting as Setting<Boolean>))
            if (setting.isList) areas.add(ListArea(parentArea, setting))
        }
        return areas
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
        val settingsHeight = (areas.sumOf { (it.totalHeight + PADDING * (it as SettingArea<*>).visibleFactor.get()).toInt() } + PADDING)
        val factoredHeight = (areas.sumOf { (it.height + PADDING * (it as SettingArea<*>).visibleFactor.get()).toInt() } + PADDING) * expandedFactor.get()
        this.height = height!! + factoredHeight

        totalHeight = height + if (expanded) settingsHeight else 0f

        val c = 0 + (50 * (1 - expandedFactor.get()) * enableFactor.get()).toInt()
        val rectangle = Builder.rectangle()
            .size(SizeState(width!!, this.height))
            .radius(QuadRadiusState(6f))
            .color(QuadColorState(fromRGB(c, c, c, 40 * showFactor.get())))
            .build()
        rectangle.render(matrix, x, y)
        val border = Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 25 * showFactor.get())))
            .radius(rectangle.radius)
            .thickness(0.2f)
            .build()
        border.render(matrix, x, y, 1f)

        val text: BuiltText = Builder.text()
            .font(bikoFont.get())
            .text(module.name)
            .color(fromRGB(255, 255, 255, 255 * showFactor.get()))
            .size(14f)
            .thickness(0.05f)
            .build()
        val textX = x + (width / 2 - text.width / 2)
        val textY = y + (height / 2 - text.size / 2)
        text.render(matrix, textX, textY)

        val borderLineWidth = text.width
        Builder.rectangle()
            .size(SizeState(borderLineWidth * enableFactor.get(), 2f))
            .radius(QuadRadiusState(1f))
            .color(QuadColorState(fromRGB(255, 255, 255, 255 * showFactor.get())))
            .build()
            .render(
                matrix,
                textX + (if (!module.enable) borderLineWidth * (1 - enableFactor.get()) else 0).toFloat(),
                textY + text.size - 3f
            )

        context.enableScissor(
            (x + 1).toInt(),
            (y + 1).toInt(),
            (x + width - 2).toInt(),
            (y + this.height - 2).toInt()
        )

        var renderY = y + this.height - factoredHeight
        if (expandedFactor.get() != 0f) {
            for (area in areas) {
                area.render(context, matrix, x + PADDING, renderY, width - PADDING * 2, null, mouseX, mouseY)
                renderY += area.height + PADDING * (area as SettingArea<*>).visibleFactor.get()
            }
        }

        context.disableScissor()

        super.render(context, matrix, x, y, width, this.height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(x, y, width, CategoryArea.MODULE_HEIGHT, mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) module.toggle()
            else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (!module.settings.isEmpty()) {
                    expanded = !expanded
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}