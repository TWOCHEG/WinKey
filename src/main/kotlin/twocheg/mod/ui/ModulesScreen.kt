package twocheg.mod.ui

import net.minecraft.client.gui.DrawContext
import org.lwjgl.glfw.GLFW
import twocheg.mod.api.modules.client.ClickGui
import twocheg.mod.utils.math.Timer

class ModulesScreen() : ScreenBase("modules") {
    var scrollX = 0f
    var scrollY = 60f
    private var velocityX = 0f
    private var velocityY = 0f

    companion object {
        const val CATEGORY_WIDTH = 200f
        const val CATEGORY_PADDING = 30f

        const val FRICTION = 0.92f
        const val KEY_ACCELERATION = 1.5f
        const val MOUSE_SENSITIVITY = 10f
        const val MAX_SPEED = 25f
    }

    private val timer: Timer = Timer()

    override val closeIf: Boolean
        get() = !open && ClickGui.categories.any { it.showFactor == 0f }

    init {
        children += ClickGui.categories
    }

    override fun close() {
        timer.reset()
        super.close()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (mc.world == null) renderPanoramaBackground(context, delta) // вот это для рендера в главном меню обязатльно\
        // оно закрывает весь предыдущий мусор который почемуто не отчишается

        velocityX *= FRICTION
        velocityY *= FRICTION
        scrollX += velocityX * delta
        scrollY += velocityY * delta
        val window = mc.window.handle
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) velocityY = (velocityY + KEY_ACCELERATION).coerceAtMost(MAX_SPEED)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) velocityY = (velocityY - KEY_ACCELERATION).coerceAtLeast(-MAX_SPEED)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) velocityX = (velocityX + KEY_ACCELERATION).coerceAtMost(MAX_SPEED)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) velocityX = (velocityX - KEY_ACCELERATION).coerceAtLeast(-MAX_SPEED)

        context.matrices.push()
        context.matrices.loadIdentity()

        val matrices = context.matrices.peek().getPositionMatrix()
        val categories = ClickGui.categories

        var startX = (width / 2 - (((CATEGORY_WIDTH + CATEGORY_PADDING) * categories.size) - CATEGORY_PADDING) / 2) + scrollX

        var closeCount = 0
        for (i in 0..<categories.size) {
            val category = categories[i]
            if (timer.passedTimeMs > 100L * (i + 1)) {
                category.show = open
            }
            if (category.showFactor == 0f) closeCount++

            category.apply {
                x = startX
                y = 100f
                recalculateLayout(CATEGORY_WIDTH, 0f)
                render(context, matrices, mouseX.toDouble(), mouseY.toDouble())
            }

            startX += CATEGORY_WIDTH + CATEGORY_PADDING
        }

        context.matrices.pop()
        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        velocityY -= (scrollY * MOUSE_SENSITIVITY).toFloat()
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }
}