package twocheg.mod.api.modules.client

import org.lwjgl.glfw.GLFW
import twocheg.mod.Categories
import twocheg.mod.managers.ModuleManager
import twocheg.mod.ui.ModulesScreen
import twocheg.mod.ui.ScreenBase
import twocheg.mod.api.modules.Parent
import twocheg.mod.ui.ConfigsScreen
import twocheg.mod.ui.ScreensFactory
import twocheg.mod.ui.impl.CategoryArea
import twocheg.mod.ui.impl.FactoryArea
import twocheg.mod.utils.math.Delta
import kotlin.collections.iterator


class ClickGui : Parent(
    "click gui",
    "screens handler",
    Categories.client,
    disableOnStartup = true,
    defaultKeyBind = GLFW.GLFW_KEY_RIGHT_SHIFT
) {
    val openFactor = Delta(this::enable)

    override var keybind: Int = config["keybind", defaultKeyBind]
        set(k) {
            if (k != -1) { // ему нельзя удалить клавишу
                config["keybind"] = k
                field = k
            }
        }

    override fun onDisable() {
        mc.currentScreen?.let { screen ->
            if (screen is ScreenBase) {
                screen.close()
            }
        }
    }

    override fun onEnable() {
        openFactor.reset() // я не знаю, дельта накапливает значение до 1 и ему арифметически и кристально поебать, потом исправлю, когда будет не похуй (никогда)
        factory.openDefault()
    }

    override fun init() {
        for ((category, moduleList) in ModuleManager.byCategory) {
            categories.add(CategoryArea(category to moduleList, 0f))
        }
        factoryArea.changeShowDelta(openFactor)
        super.init()
    }

    companion object Components {
        val categories = mutableListOf<CategoryArea>()

        val factory = ScreensFactory(
            ModulesScreen::class.java,
            ConfigsScreen::class.java
        )
        val factoryArea = FactoryArea(factory)

        fun reset() {
            categories.forEach {
                it.show = false
                it.showFactor = 0f
            }
        }
    }
}