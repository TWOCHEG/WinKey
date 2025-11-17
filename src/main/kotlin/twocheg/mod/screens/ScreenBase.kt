package twocheg.mod.screens

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.BufferRenderer
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.managers.ModuleManager
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.modules.Parent
import twocheg.mod.modules.client.ClickGui
import twocheg.mod.utils.math.ColorUtils.fromRGB


open class ScreenBase(open val name: String) : Screen(Text.literal(name)) {
    companion object {
        val mc: MinecraftClient = MinecraftClient.getInstance()
    }

    val gui = ModuleManager.get(ClickGui::class.java)!!

    val open: Boolean
        get() = gui.enable
    val openFactor
        get() = gui.openFactor

    val areas = mutableListOf<RenderArea>()

    init {
        areas.add(gui.selectScreens)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (closeIf()) Parent.mc.setScreen(null)

        context.matrices.push()
        context.matrices.loadIdentity()

        val matrix = context.matrices.peek().getPositionMatrix()

        val bgColorTop = fromRGB(0, 0, 0, 80 * gui.openFactor.get())
        val bgColorBottom = fromRGB(0, 0, 0, 0)

        Builder.rectangle()
            .size(SizeState(width.toDouble(), height.toDouble()))
            .color(QuadColorState(bgColorTop, bgColorBottom, bgColorBottom, bgColorTop))
            .build()
            .render(matrix, 0f, 0f, 1f)

        areas[0].render(
            context, matrix,
            0f, 0f, null, null, mouseX.toDouble(), mouseY.toDouble()
        ) // рендеринг выбора экранов

        context.matrices.pop()
        BufferRenderer.reset()
    }

    override fun shouldPause(): Boolean = false

    override fun close() {

    }

    open fun closeIf(): Boolean = !open && openFactor.get() == 0f

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (area in areas) {
            if (area.keyPressed(keyCode, scanCode, modifiers)) return true
        }
        if (gui.openFactor.get() == 1f && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == gui.keybind)) {
            gui.enable = false
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        for (area in areas) {
            if (area.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (area in areas) {
            if (area.mouseClicked(mouseX, mouseY, button)) return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        for (area in areas) {
            if (area.charTyped(chr, modifiers)) return true
        }
        return super.charTyped(chr, modifiers)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (area in areas) {
            if (area.mouseReleased(mouseX, mouseY, button)) return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        for (area in areas) {
            if (area.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }
}