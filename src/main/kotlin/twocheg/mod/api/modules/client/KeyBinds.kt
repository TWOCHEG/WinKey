package twocheg.mod.api.modules.client

import meteordevelopment.orbit.EventHandler
import net.minecraft.client.gui.screen.TitleScreen
import twocheg.mod.Categories
import twocheg.mod.events.impl.EventKeyPress
import twocheg.mod.managers.ModuleManager
import twocheg.mod.api.modules.Parent

class KeyBinds : Parent(
    "keybinds",
    category = Categories.client,
    visibleInUI = false,
    enabledByDefault = true
) {
    @EventHandler
    @Suppress("unused")
    private fun keyPress(e: EventKeyPress) {
        if (mc.currentScreen == null || mc.currentScreen is TitleScreen) {
            ModuleManager.modules.forEach {
                if (e.keyCode == it.keybind) it.toggle()
            }
        }
    }
}