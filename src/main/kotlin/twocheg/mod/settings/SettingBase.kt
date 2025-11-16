package twocheg.mod.settings

import twocheg.mod.managers.ConfigManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class SettingBase<T>(
    override val name: String,
    override val default: T,
) : Setting<T>, ReadWriteProperty<Any?, T> {
    var visibility: () -> Boolean = { true }

    private lateinit var config: ConfigManager
    private var _value: T = default

    override var value: T
        get() = if (::config.isInitialized) get() else _value
        set(v) = set(v)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun reset() {
        value = default
    }

    override fun get(): T {
        check(::config.isInitialized) { "ConfigManager not initialized. Call init() first." }
        return config[name, default]
    }

    override fun set(v: T) {
        _value = v
        if (::config.isInitialized) {
            config[name] = v
        }
    }

    override fun init(config: ConfigManager) {
        this.config = config.sub(name)
        _value = this.config[name, default]
    }
}
