package twocheg.mod.modules.client

import meteordevelopment.orbit.EventHandler
import org.lwjgl.glfw.GLFW
import twocheg.mod.Categories
import twocheg.mod.events.impl.EventKeyPress
import twocheg.mod.moduleManager
import twocheg.mod.screens.ConfigsScreen
import twocheg.mod.screens.ModulesScreen
import twocheg.mod.screens.ScreenBase
import twocheg.mod.screens.impl.modules.SelectScreensArea
import twocheg.mod.modules.Parent
import twocheg.mod.screens.impl.modules.CategoryArea
import twocheg.mod.utils.math.Delta


class ClickGui : Parent("click gui", Categories.client, "screens handler", keybind = GLFW.GLFW_KEY_RIGHT_SHIFT) {
    val showFactor = Delta({ enable })

    var selectScreens = SelectScreensArea(ModulesScreen::class.java, ConfigsScreen::class.java)
    val categories = mutableListOf<CategoryArea>()

    init {
        setEnable(false)

        this.selectScreens.showFactor = showFactor
    }

    override fun setKeybind(code: Int) {
        if (code == -1) return
        super.setKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT)
    }

    override fun onDisable() {
        mc.currentScreen?.let { screen ->
            if (screen is ScreenBase) {
                screen.close()
            }
        }
    }

    override fun onEnable() {
        if (categories.isEmpty()) {
            for ((category, moduleList) in moduleManager.getSorted()) {
                if (category == null) continue
                categories.add(CategoryArea(category, moduleList))
            }
        }
        resetComponents()
        selectScreens.reset()
        mc.setScreen(selectScreens.createGui(selectScreens.defaultGuiClass))
    }

    @EventHandler
    @Suppress("unused")
    fun onKeyPress(e: EventKeyPress) {
        if (enable && e.keyCode == GLFW.GLFW_KEY_SPACE) setEnable(false)
    }

    fun resetComponents() {
        for (category in categories) {
            category.reset()
        }
    }
}