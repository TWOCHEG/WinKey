package twocheg.mod.screens.impl

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.texture.AbstractTexture
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.identifier
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.utils.math.CurveType
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.Lerp
import twocheg.mod.utils.math.fromRGB
import java.awt.Color


class ModuleSearchArea(override val parentArea: RenderArea, val onInter: (Boolean) -> Unit) : RenderArea(parentArea) {
    var isActive = false
    val pulse = Delta({ isActive }, mode = CurveType.Pulse)
    val pulseX = Lerp(0f, 75)

    var q = ""

    init {
        this.width = 14 + PADDING * 2
        this.height = 14 + PADDING * 2
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
        val abstractTexture: AbstractTexture = mc.textureManager.getTexture(identifier("textures/ui/search.png"))
        Builder.texture()
            .size(SizeState(this.width - PADDING * 2, this.height - PADDING * 2))
            .texture(0f, 0f, 1f, 1f, abstractTexture)
            .color(QuadColorState(fromRGB(255, 255, 255, 150 * parentArea.showFactor.get())))
            .build()
            .render(matrix, x + PADDING, y + PADDING, zIndex)

        if (showFactor.get() != 0f) {
            if (isActive) {
                val text: BuiltText = Builder.text()
                    .font(bikoFont.get())
                    .text(q.ifEmpty { "..." })
                    .color(fromRGB(255, 255, 255, 200 * showFactor.get()))
                    .size(14f)
                    .thickness(0.05f)
                    .build()
                text.render(matrix, x + this.width, y + PADDING, zIndex)
                val targetDiff = text.width + 1f
                if (pulseX.get() == 0f) pulseX.forceSet(targetDiff)
                else pulseX.set(targetDiff)
            }

            Builder.rectangle()
                .size(SizeState(3f, 14f))
                .color(
                    QuadColorState(
                        fromRGB(255, 255, 255, 200 * pulse.get() * showFactor.get())
                    )
                )
                .radius(QuadRadiusState(1.5f))
                .build()
                .render(matrix, x + this.width + pulseX.get(), y + PADDING, zIndex)
        }
        super.render(context, matrix, x, y, this.width, this.height, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isHovered(mouseX, mouseY)) {
            isActive = true
            onInter(true)
            q = ""
            pulseX.forceSet(0f)
            return true
        } else if (isActive) {
            isActive = false
            onInter(false)
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (isActive && chr.isLetter() && chr in 'a'..'z' || chr in 'A'..'Z') {
            q += chr
            return true
        }
        return super.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) q = q.dropLast(1)
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                isActive = false
                onInter(false)
            }
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}