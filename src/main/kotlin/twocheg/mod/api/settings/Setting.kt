package twocheg.mod.api.settings

import twocheg.mod.managers.ConfigManager

interface Setting<T> {
    val name: String
    val default: T
    var value: T
    fun set(v: T)
    fun get(): T
    fun reset()
    fun init(config: ConfigManager)
}