package twocheg.mod.ui.impl

import net.minecraft.client.gui.DrawContext
import org.joml.Matrix4f
import twocheg.mod.Categories
import twocheg.mod.api.modules.Module
import twocheg.mod.api.render.VerticalContainerArea
import twocheg.mod.utils.math.Spring
import twocheg.mod.utils.math.ColorUtils.fromRGB

import com.ferra13671.cometrenderer.CometRenderer
import com.ferra13671.cometrenderer.vertex.DrawMode
import com.ferra13671.cometrenderer.vertex.mesh.MeshBuilder
import net.minecraft.client.util.BufferAllocator
import twocheg.mod.api.render.RenderFormats

class CategoryArea(
    val data: Pair<Categories, List<Module>>
) : VerticalContainerArea(0f) {
    companion object {
        const val RADIUS = 5f
    }

    var targetHeight by Spring(0f)

    override fun render(
        context: DrawContext,
        matrices: Matrix4f,
        mouseX: Double,
        mouseY: Double
    ) {
        val yOffset = -100f * (1f - showFactor)
        val currentY = y + yOffset

        val color = fromRGB(100, 100, 100, 100 * showFactor)

        // блять помогите
    }

    override fun recalculateLayout(availableWidth: Float, availableHeight: Float) {
        height = 100f
        targetHeight = height
    }
}