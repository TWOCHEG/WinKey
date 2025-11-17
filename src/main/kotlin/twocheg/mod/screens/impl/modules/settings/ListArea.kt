package twocheg.mod.screens.impl.modules.settings

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.screens.impl.SelectionArea
import twocheg.mod.screens.impl.ValueArea
import twocheg.mod.settings.ListSettings
import twocheg.mod.settings.Setting
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.ColorUtils.fromRGB
import twocheg.mod.utils.math.MathUtils.splitText

class ListArea (
    override val parentArea: RenderArea,
    val list: ListSettings<*>
) : SettingArea<ListSettings<*>>(parentArea, list) {
    val selection = SelectionArea(this)

    init {
        @Suppress("CAST_NEVER_SUCCEEDS")
        for (option in list.values) {
            areas.add(ValueArea(
                this, option,
                list::setAny,
                { list.current },
                option.toString()
            ))
        }
        selection.targetArea = { getValueArea(list.current) }
    }

    var expanded = false
    val expandedFactor = Delta({ expanded })

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
        val lines = splitText(
            setting.name,
            width!!
        ) { text -> bikoFont().getWidth(text, 14f) }
        var renderY = y
        for (line in lines) {
            val text: BuiltText = Builder.text()
                .font(bikoFont())
                .text(line)
                .color(fromRGB(255, 255, 255, 200 * showFactor.get()))
                .size(14f)
                .thickness(0.05f)
                .build()
            text.render(matrix, x, renderY, zIndex)
            renderY += text.size + 2f
        }
        renderY += 2f

        selection.render(context, matrix, x, y, null, null, mouseX, mouseY)

        var renderX = x
        for (area in areas) {
            if (renderX - x + area.width > width) {
                renderY += area.height + PADDING
                renderX = x
            }
            area.render(context, matrix, renderX, renderY, null, null, mouseX, mouseY)
            renderX += area.width + PADDING
        }
        renderY += areas.last().height

        super.render(context, matrix, x, y, width, renderY - y, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && isHovered(mouseX, mouseY)) {
            expanded = !expanded
            return true
        }
        if (expanded) {
            return super.mouseClicked(mouseX, mouseY, button)
        }
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (scrollY < 0) list.prev()
        else list.next()
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }
}