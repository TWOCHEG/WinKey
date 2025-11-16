package twocheg.mod.modules

import net.minecraft.client.MinecraftClient
import twocheg.mod.Categories
import twocheg.mod.managers.ConfigManager
import twocheg.mod.managers.ModuleManager
import kotlin.reflect.KProperty

data class KeyBind(
    val key: Int,
    val modifiers:
    Int = 0,
) {
    companion object {
        val NONE = KeyBind(-1)
    }
}

abstract class Parent(
    val name: String,
    val description: String?,
    val category: Categories,
    val enabledByDefault: Boolean = false,
    val disableOnStartup: Boolean = false,
    val visibleInUI: Boolean = true,
    val defaultKeyBind: KeyBind = KeyBind.NONE
) {
    companion object {
        @JvmStatic
        val mc: MinecraftClient = MinecraftClient.getInstance()

        @JvmStatic
        fun fullNullCheck(): Boolean = mc.player == null || mc.world == null
    }

    private val config = ConfigManager("modules.$name")

    var enable: Boolean = config["enabled", if (disableOnStartup) false else enabledByDefault]
    open var keyBind: KeyBind = defaultKeyBind

    open fun onEnable() {}
    open fun onDisable() {}
    open fun onToggle() {}

    fun toggle() {
        enable = !enable
        onToggle()
        if (enable) onEnable() else onDisable()
    }

    fun resetToDefault() {
        enable = enabledByDefault
        keyBind = defaultKeyBind
    }

    init {
        ModuleManager.register(this)
    }
}
