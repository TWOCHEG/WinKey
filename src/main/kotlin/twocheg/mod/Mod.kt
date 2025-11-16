package twocheg.mod

import com.google.common.base.Suppliers
import meteordevelopment.orbit.EventBus
import meteordevelopment.orbit.IEventBus
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import twocheg.mod.managers.ModuleManager
import twocheg.mod.msdf.MsdfFont
import java.lang.invoke.MethodHandles

import twocheg.mod.modules.Example
import twocheg.mod.modules.client.*


val EVENT_BUS: IEventBus = EventBus()
const val VERSION = "0.0.1"
const val NAME_SPACE = "twocheg.mod"

enum class Categories {
    combat, client, example, render, misc
}

val bikoFont: () -> MsdfFont = { MsdfFont.builder().atlas("biko").data("biko").build() }

@Suppress("unused")
fun init() {
    EVENT_BUS.registerLambdaFactory(NAME_SPACE) { lookupInMethod, klass ->
        lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup?
    }

    KeyBinds()
    ClickGui()
    Example()

    ModuleManager.init()
}

fun isFuturePresent(): Boolean = FabricLoader.getInstance().getModContainer("future").isPresent

fun identifier(path: String): Identifier {
    return Identifier.of(NAME_SPACE.split(".").last(), path)
}
