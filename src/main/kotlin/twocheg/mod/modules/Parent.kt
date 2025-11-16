package twocheg.mod.modules

import net.minecraft.client.MinecraftClient
import twocheg.mod.Categories
import twocheg.mod.managers.ConfigManager
import twocheg.mod.managers.ModuleManager
import twocheg.mod.settings.Setting
import twocheg.mod.settings.SettingBase
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

abstract class Parent(
    val name: String,
    val description: String? = null,
    val category: Categories,
    val enabledByDefault: Boolean = false,
    val disableOnStartup: Boolean = false,
    val visibleInUI: Boolean = true,
    var defaultKeyBind: Int = -1
) {
    companion object {
        @JvmStatic
        val mc: MinecraftClient = MinecraftClient.getInstance()

        @JvmStatic
        fun fullNullCheck(): Boolean = mc.player == null || mc.world == null
    }

    val settings = mutableListOf<Setting<*>>()

    protected val config = ConfigManager("modules.$name")

    var enable: Boolean = if (disableOnStartup) false else config["enabled", enabledByDefault]
        set(e) {
            config["enable"] = e
            field = e
        }
    open var keyBind: Int = config["keybind", defaultKeyBind]
        set(k) {
            config["keybind"] = k
            field = k
        }

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

    open fun init() {
        this::class.declaredMemberProperties.forEach { prop ->
            prop.isAccessible = true
            val value = prop.getter.call(this)
            if (value is Setting<*>) {
                settings.add(value)
                value.init(this.config)
            }
        }
    }

    init {
        ModuleManager.register(this)
    }
}
