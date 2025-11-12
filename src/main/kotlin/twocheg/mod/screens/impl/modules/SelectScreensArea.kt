package twocheg.mod.screens.impl.modules

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import org.joml.Matrix4f
import twocheg.mod.builders.Builder
import twocheg.mod.builders.states.QuadColorState
import twocheg.mod.builders.states.QuadRadiusState
import twocheg.mod.builders.states.SizeState
import twocheg.mod.moduleManager
import twocheg.mod.modules.Parent
import twocheg.mod.modules.client.ClickGui
import twocheg.mod.renderers.impl.BuiltBlur
import twocheg.mod.screens.impl.RenderArea
import twocheg.mod.screens.impl.ValueArea
import twocheg.mod.utils.math.Delta
import twocheg.mod.utils.math.Lerp
import twocheg.mod.utils.math.fromRGB
import java.lang.reflect.Constructor
import java.util.Arrays

class SelectScreensArea<T : Class<out Screen>>(vararg val guiClasses: T) : RenderArea() {
    var currentGuiClass: T
    val defaultGuiClass: T

    val changeTime = 75L

    val targetX = Lerp(0f, changeTime)
    val targetY = Lerp(0f, changeTime)
    val targetWidth = Lerp(0f, changeTime)
    val targetHeight = Lerp(0f, changeTime)

    var isSearch = false
    val searchDelta = Delta({ isSearch })

    init {
        zIndex = 10f

        areas.clear()

        val defaultClass: T = Arrays.stream(guiClasses).toList().first()

        this.currentGuiClass = defaultClass
        this.defaultGuiClass = defaultClass

        for (c in guiClasses) {
            areas.add(
                ValueArea(
                    this,
                    c, this::setScreen,
                    { currentGuiClass },
                    c.simpleName
                )
            )
        }
        areas.add(ModuleSearchArea(this) { isActive ->
            isSearch = isActive
            areas.last().show = isActive
        })

        this.y = 10f
    }

    fun reset() {
        val defaultClass: T = Arrays.stream(guiClasses).toList().first()
        this.currentGuiClass = defaultClass
    }

    fun setScreen(guiClass: T) {
        mc.setScreen(createGui(guiClass))
        currentGuiClass = guiClass
        moduleManager.get(ClickGui::class.java)!!.resetComponents()
        setCords()
    }

    fun setCords() {
        targetX.set(getValueArea(currentGuiClass)!!.x)
        targetY.set(getValueArea(currentGuiClass)!!.y)
        targetWidth.set(getValueArea(currentGuiClass)!!.width)
        targetHeight.set(getValueArea(currentGuiClass)!!.height)
    }

    fun createGui(guiClass: Class<out Screen>): Screen {
        val constructor: Constructor<out Screen> = guiClass.getConstructor()
        return constructor.newInstance()
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
        this.width = PADDING + areas.sumOf { (it.width + PADDING).toInt() }
        for (area in areas) {
            if (area.height + PADDING * 2 > this.height) this.height = area.height + PADDING * 2
        }
        this.x = ((mc.window.width / mc.window.scaleFactor) / 2 - this.width / 2).toFloat()

        val blur: BuiltBlur = Builder.blur()
            .size(SizeState(this.width, this.height))
            .radius(QuadRadiusState(6f))
            .blurRadius(12f)
            .color(QuadColorState(fromRGB(110, 110, 110, 255 * showFactor.get())))
            .build()
        blur.render(matrix, this.x, this.y, zIndex)
        Builder.border()
            .size(blur.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 10 * showFactor.get())))
            .radius(QuadRadiusState(6f))
            .thickness(0.2f)
            .build()
            .render(matrix, this.x, this.y, zIndex + 1)

        context.enableScissor(
            (this.x + 1).toInt(),
            (this.y + 1).toInt(),
            (this.x + this.width - 1).toInt(),
            (this.y + this.height - 1).toInt()
        )

        val rectangle = Builder.rectangle()
            .size(SizeState(targetWidth.get(), targetHeight.get()))
            .color(QuadColorState(fromRGB(0, 0, 0, 50 * showFactor.get())))
            .radius(QuadRadiusState(6f))
            .build()
        rectangle.render(matrix, targetX.get(), targetY.get(), 2f)
        Builder.border()
            .size(rectangle.size)
            .color(QuadColorState(fromRGB(255, 255, 255, 10 * showFactor.get())))
            .radius(rectangle.radius())
            .thickness(0.1f)
            .build()
            .render(matrix, targetX.get(), targetY.get(), zIndex + 1)

        if (searchDelta.get() < 0.1f) setCords()
        else {
            targetX.set(this.x + PADDING)
            targetY.set(this.y + PADDING)
            targetWidth.set(this.width - PADDING * 2)
            targetHeight.set(this.height - PADDING * 2)
        }

        var valuesRenderX = this.x + PADDING - (this.width - areas.last().width - PADDING * 2) * searchDelta.get()
        for (area in areas) {
            area.render(context, matrix, valuesRenderX, this.y + PADDING, width, height, mouseX, mouseY)
            valuesRenderX += area.width + PADDING
        }

        context.disableScissor()

        super.render(context, matrix, this.x, this.y, this.width, this.height, mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (isHovered(mouseX, mouseY)) {
            var i = guiClasses.indexOf(currentGuiClass) + scrollY.toInt()
            if (i < 0) i = guiClasses.size - 1
            else if (i > guiClasses.size - 1) i = 0
            setScreen(guiClasses[i])
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }
}