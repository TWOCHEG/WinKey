package twocheg.mod.managers

import meteordevelopment.orbit.EventHandler
import twocheg.mod.Categories
import twocheg.mod.EVENT_BUS
import twocheg.mod.events.impl.EventDisableModule
import twocheg.mod.events.impl.EventEnableModule
import twocheg.mod.modules.Parent

object ModuleManager {
    private val modules = mutableListOf<Parent>()
    val byCategory = mutableMapOf<Categories, MutableList<Parent>>()

    fun register(module: Parent) {
        modules.add(module)
        byCategory.getOrPut(module.category) { mutableListOf() }.add(module)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Parent> get(c: Class<T>): T? {
        for (m in modules) {
            if (c == m.javaClass) return m as T
        }
        return null
    }

    fun getByCategory(category: Categories): List<Parent> = byCategory[category] ?: emptyList()
    fun getCategories(): Set<Categories> = byCategory.keys

    fun enableAll() = modules.forEach { it.enable = true }
    fun disableAll() = modules.forEach { it.enable = false }
    fun resetAll() = modules.forEach { it.resetToDefault() }

    fun getEnabled(): List<Parent> = modules.filter { it.enable }

    fun init() {
        EVENT_BUS.subscribe(this)
        getEnabled().forEach { EVENT_BUS.subscribe(it) }
    }

    @EventHandler
    @Suppress("unused")
    fun onEnableModule(e: EventEnableModule) {
        EVENT_BUS.subscribe(e.module)
    }

    @EventHandler
    @Suppress("unused")
    fun onDisableModule(e: EventDisableModule) {
        EVENT_BUS.unsubscribe(e.module)
    }
}
