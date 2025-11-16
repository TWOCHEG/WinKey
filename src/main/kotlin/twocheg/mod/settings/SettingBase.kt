package twocheg.mod.settings

import twocheg.mod.managers.ConfigManager

class SettingBase<T>(
    override val name: String,
    override val default: T,
) : Setting<T> {
    override var value: T
        set(v) {
            config[name] = v
        }
        get() = get()

    private var config: ConfigManager = ConfigManager() // not

    override fun reset() {
        value = default
    }

    override fun get(): T {
        return config[name, default]
    }

    override fun set(v: T) {
        value = v
    }

    override fun init(config: ConfigManager) {
        this.config = config.sub(name)
        set(config[name, default])
    }
}
