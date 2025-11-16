package twocheg.mod.settings

import twocheg.mod.managers.ConfigManager
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

// этот класс наследуется от настройки чтобы было удобнее взаимодействовать с группами
// еще желательно забудьте и не обращаете на это внимания
abstract class SettingGroup(
    name: String? = null,
) : SettingBase<Unit>("", Unit) {
    override val name = name ?: this::class.simpleName!!.trim()
    internal val settings = mutableListOf<Setting<*>>()

    private var config: ConfigManager = ConfigManager()

    override fun init(config: ConfigManager) {
        this.config = config.sub(name)

        this::class.declaredMemberProperties.forEach { prop ->
            prop.isAccessible = true
            val value = prop.getter.call(this)
            if (value is Setting<*>) {
                settings.add(value)
                value.init(this.config)
            }
        }
    }
}