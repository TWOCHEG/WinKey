package twocheg.mod.settings

import twocheg.mod.managers.ConfigManager
import kotlin.reflect.jvm.isAccessible

abstract class SettingGroup(val name: String) : SettingBase<Void>() {
    internal val settings = mutableListOf<Setting<*>>()

    private var config: ConfigManager = ConfigManager() // not

    fun init(config: ConfigManager) {
        this.config = config.sub(name)

        this::class.members.forEach { property ->
            property.isAccessible = true

            if (property is Setting<*>) {
                settings.add(property)
                property.init(this.config)
            }
        }
    }
}