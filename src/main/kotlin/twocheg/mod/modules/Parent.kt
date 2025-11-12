package twocheg.mod.modules

import net.minecraft.client.MinecraftClient
import twocheg.mod.Categories
import twocheg.mod.EVENT_BUS
import twocheg.mod.events.impl.EventDisableModule
import twocheg.mod.events.impl.EventEnableModule
import twocheg.mod.managers.ConfigManager
import twocheg.mod.settings.Setting

abstract class Parent(
    val name: String,
    private val category: Categories?,
    var description: String? = null,  // var потому что может в будущем буду шалить
    enable: Boolean = false,
    keybind: Int = -1
) {
    val config = ConfigManager(name)
    var enable: Boolean = getValue(ConfigManager.ENABLE_KEY, enable)
        private set
    var keybindCode: Int = getValue(ConfigManager.KEYBIND_KEY, keybind)

    val settings: List<Setting<*>> by lazy {
        val list = mutableListOf<Setting<*>>()
        var currentClass: Class<*>? = this::class.java
        while (currentClass != null) {
            for (field in currentClass.declaredFields) {
                if (!Setting::class.java.isAssignableFrom(field.type)) continue
                try {
                    field.isAccessible = true
                    field.get(this)?.let { setting ->
                        if (setting is Setting<*>) {
                            list.add(setting)
                        }
                    }
                } catch (_: IllegalAccessException) {}
            }
            currentClass = currentClass.superclass
        }
        list.forEach { it.init(config) }
        list
    }

    init {
        setEnable(enable)
        setKeybind(keybindCode)
    }

    fun onSettingUpdate(setting: Setting<*>) {}

    protected open fun onEnable() {}

    protected open fun onDisable() {}

    fun isHiddenModule(): Boolean = category == null

    fun getCategory(): Categories? = category

    fun toggle() {
        if (isToggleable()) {
            setEnable(!this@Parent.enable)
        }
    }

    fun setEnable(value: Boolean) {
        config.set(ConfigManager.ENABLE_KEY, value)
        this@Parent.enable = value

        if (this@Parent.enable) {
            EVENT_BUS.post(EventEnableModule(this))
            onEnable()
        } else {
            EVENT_BUS.post(EventDisableModule(this))
            onDisable()
        }
    }

    open fun setKeybind(code: Int) {
        config.set(ConfigManager.KEYBIND_KEY, code)
        keybindCode = code
    }

    fun setValue(key: String, value: Any?) {
        config.set(key, value)
    }

    inline fun <reified T> getValue(name: String, defaultValue: T): T {
        @Suppress("UNCHECKED_CAST")
        return config.get(name, defaultValue)
    }

    fun resetSettings() {
        settings.forEach { it.resetToDefault() }
    }

    companion object {
        @JvmStatic
        val mc: MinecraftClient = MinecraftClient.getInstance()

        @JvmStatic
        fun fullNullCheck(): Boolean = mc.player == null || mc.world == null
    }

    open fun isToggleable(): Boolean = true
}