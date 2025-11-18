package twocheg.mod.ui

import net.minecraft.client.gui.DrawContext
import twocheg.mod.bikoFont
import twocheg.mod.builders.Builder
import twocheg.mod.renderers.impl.BuiltText
import twocheg.mod.utils.math.ColorUtils.fromRGB


class ConfigsScreen() : ScreenBase("configs") {
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (mc.world == null) renderPanoramaBackground(context, delta)

        context.matrices.push()
        context.matrices.loadIdentity()

        val matrix = context.matrices.peek().getPositionMatrix()

        val text: BuiltText = Builder.text()
            .font(bikoFont())
            .text("SOON...")
            .color(fromRGB(255, 255, 255, (255 * openFactor.get()).toInt()))
            .size(20f)
            .thickness(0.05f)
            .build()
        text.render(matrix, width / 2 - text.getWidth() / 2, (height / 2 - text.size) - 50 * (1 - openFactor.get()), 2f)

        context.matrices.pop()

        super.render(context, mouseX, mouseY, delta)
    }
}