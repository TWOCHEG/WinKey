package twocheg.mod.screens

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.BufferRenderer
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.moduleManager
import twocheg.mod.modules.Parent
import twocheg.mod.modules.client.ClickGui
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.fromRGB


open class ScreenBase(open val name: String) : Screen(Text.literal(name)) {
    companion object {
        val mc: MinecraftClient = MinecraftClient.getInstance()
    }

    var open = true
    var openFactor = Delta({ open })

    val gui = moduleManager.get(ClickGui::class.java)!!

    val areas = mutableListOf<RenderArea>()

    init {
        areas.add(gui.selectScreens)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (closeIf()) Parent.mc.setScreen(null)

        context.matrices.push()
        context.matrices.loadIdentity()

        val matrix = context.matrices.peek().getPositionMatrix()

        val bgColorTop = fromRGB(0, 0, 0, 80 * gui.showFactor.get())
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
        open = false
    }

    open fun closeIf(): Boolean = !open && openFactor.get() == 0f

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (area in areas) {
            if (area.keyPressed(keyCode, scanCode, modifiers)) return true
        }
        if (gui.showFactor.get() == 1f && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == gui.keybindCode)) {
            gui.setEnable(false)
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