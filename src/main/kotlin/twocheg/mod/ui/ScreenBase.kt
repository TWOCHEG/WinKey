package twocheg.mod.ui

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
import twocheg.mod.api.modules.Parent
import twocheg.mod.api.modules.client.ClickGui
import twocheg.mod.api.render.RenderArea
import twocheg.mod.utils.math.ColorUtils.fromRGB


open class ScreenBase(open val name: String) : Screen(Text.literal(name)) {
    companion object {
        val mc: MinecraftClient = MinecraftClient.getInstance()

        const val FACTORY_HEIGHT = 30f
        const val FACTORY_Y = 10f
    }

    open val closeIf: Boolean
        get() = !open && openFactor.get() == 0f

    val gui = ModuleManager.get(ClickGui::class.java)!!

    var open: Boolean
        get() = gui.enable
        set(e) { gui.enable = e }
    val openFactor
        get() = gui.openFactor

    val children = mutableListOf<RenderArea>()

    init {
        children += ClickGui.factoryArea
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (closeIf) Parent.mc.setScreen(null)

        context.matrices.push()
        context.matrices.loadIdentity()

        val matrices = context.matrices.peek().getPositionMatrix()

        val bgColorTop = fromRGB(0, 0, 0, 80 * gui.openFactor.get())
        val bgColorBottom = fromRGB(0, 0, 0, 0)

        Builder.rectangle()
            .size(SizeState(width.toDouble(), height.toDouble()))
            .color(QuadColorState(bgColorTop, bgColorBottom, bgColorBottom, bgColorTop))
            .build()
            .render(matrices, 0f, 0f, -1f)

        children[0].apply {
            recalculateLayout(0f, FACTORY_HEIGHT)
            x = this@ScreenBase.width / 2 - width / 2
            y = FACTORY_Y
            render(context, matrices, mouseX.toDouble(), mouseY.toDouble())
        }

        context.matrices.pop()
        BufferRenderer.reset()
    }

    override fun shouldPause(): Boolean = false

    override fun close() {

    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (area in children) {
            if (area.keyPressed(keyCode, scanCode, modifiers)) return true
        }
        if (openFactor.get() > 0.1 && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == gui.keybind)) {
            open = false
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        for (area in children) {
            if (area.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (area in children) {
            if (area.mouseClicked(mouseX, mouseY, button)) return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        for (area in children) {
            if (area.charTyped(chr, modifiers)) return true
        }
        return super.charTyped(chr, modifiers)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (area in children) {
            if (area.mouseReleased(mouseX, mouseY, button)) return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        for (area in children) {
            if (area.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }
}