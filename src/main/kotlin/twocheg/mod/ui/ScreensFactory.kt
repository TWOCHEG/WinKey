package twocheg.mod.ui

import net.minecraft.client.gui.screen.Screen
import twocheg.mod.api.modules.Parent
import twocheg.mod.api.modules.client.ClickGui

class ScreensFactory<T : Class<out ScreenBase>>(vararg classes: T) {
    val classList = classes.toList()

    var currentClass: T = classList.first()
        set(value) {
            field = value
            openCurrent()
            ClickGui.reset()
        }

    fun create(clazz: T): Screen {
        return clazz.getDeclaredConstructor()
            .apply { isAccessible = true }
            .newInstance() as Screen
    }

    @Suppress("UNCHECKED_CAST")
    fun setAny(clazz: Any) {
        currentClass = clazz as T
    }

    fun openCurrent() = Parent.mc.setScreen(create(currentClass))

    fun openDefault() {
        currentClass = classList.first()
        Parent.mc.setScreen(create(classList.first()))
    }
}