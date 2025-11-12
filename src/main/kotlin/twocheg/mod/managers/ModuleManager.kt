package twocheg.mod.managers

import twocheg.mod.Categories
import twocheg.mod.EVENT_BUS
import twocheg.mod.modules.Parent

import meteordevelopment.orbit.EventHandler
import twocheg.mod.events.impl.EventDisableModule
import twocheg.mod.events.impl.EventEnableModule

class ModuleManager(vararg modules: Parent) {
    val modules = modules.toList()

    fun getSorted(): Map<Categories?, List<Parent>> {
        return modules
            .groupBy { it.getCategory() }
            .mapValues { (_, moduleList) ->
                moduleList.sortedBy { it.name }
            }
            .toSortedMap(compareBy { it?.name })
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Parent> get(c: Class<T>): T? {
        for (m in modules) {
            if (c == m.javaClass) return m as T
        }
        return null
    }

    fun enabledModules(): List<Parent> {
        return modules.filter { it.enable }
    }

    fun init() {
        EVENT_BUS.subscribe(this)
        enabledModules().forEach { EVENT_BUS.subscribe(it) } // да я не буду скрывать что я еблан и не умею делать нормальные архитектуры
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