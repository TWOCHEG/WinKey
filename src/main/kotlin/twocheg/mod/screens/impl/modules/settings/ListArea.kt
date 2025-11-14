package twocheg.mod.screens.impl.modules.settings

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.screens.impl.ValueArea
import twocheg.mod.settings.Setting
import twocheg.mod.utils.math.Delta

class ListArea (
    override val parentArea: RenderArea,
    listSet: Setting<*>
) : SettingArea<Setting<*>>(parentArea, listSet) {
    init {
        for (option in listSet.getOptions()!!) {
            areas.add(ValueArea(
                this, option,
                listSet::setValue,
                listSet::getValue,
                option.toString()
            ))
        }
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
        // TODO
        super.render(context, matrix, x, y, width, height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && isHovered(mouseX, mouseY)) {
            expanded = !expanded
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}