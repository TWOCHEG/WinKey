package twocheg.mod.screens

import net.minecraft.client.gui.DrawContext
import org.lwjgl.glfw.GLFW
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

    init {
        areas += gui.categories
    }

    override fun close() {
        timer.reset()
        super.close()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (mc.world == null) renderPanoramaBackground(context, delta) // вот эта вот хуйня спасает весь интерфейс если он открыт в главном меню, не убирайте это, пожалуйста, никто
        // если вкратце походу буфер не отчищается, и это просто рендерится поверх всего предыдущего мусора, закрывая его

        context.matrices.push()
        context.matrices.loadIdentity()

        velocityX *= FRICTION
        velocityY *= FRICTION
        scrollX += velocityX * delta
        scrollY += velocityY * delta

        val window = mc.window.handle
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) velocityY = (velocityY + KEY_ACCELERATION).coerceAtMost(MAX_SPEED)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) velocityY = (velocityY - KEY_ACCELERATION).coerceAtLeast(-MAX_SPEED)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) velocityX = (velocityX + KEY_ACCELERATION).coerceAtMost(MAX_SPEED)
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) velocityX = (velocityX - KEY_ACCELERATION).coerceAtLeast(-MAX_SPEED)

        val matrix = context.matrices.peek().getPositionMatrix()
        val categories = gui.categories

        var startX = (width / 2 - (((CATEGORY_WIDTH + CATEGORY_PADDING) * categories.size) - CATEGORY_PADDING) / 2) + scrollX

        var closeCount = 0
        for (i in 0..<categories.size) {
            val c = categories[i]
            if (timer.passedTimeMs > 100L * (i + 1)) {
                c.show = open
            }
            if (c.showFactor.get() == 0f) closeCount++

            c.render(context, matrix, startX, scrollY, CATEGORY_WIDTH, null, mouseX.toDouble(), mouseY.toDouble())
            startX += CATEGORY_WIDTH + CATEGORY_PADDING
        }

        context.matrices.pop()
        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        velocityY -= (scrollY * MOUSE_SENSITIVITY).toFloat()
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun closeIf(): Boolean = !open && gui.categories.any { it.showFactor.get() == 0f }
}