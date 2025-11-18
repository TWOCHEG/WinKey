package twocheg.mod.api.modules

import org.lwjgl.glfw.GLFW
import twocheg.mod.Categories
import twocheg.mod.api.settings.BooleanSetting
import twocheg.mod.api.settings.ListSettings
import twocheg.mod.api.settings.SettingGroup

data class MyGroup(
    val bool: BooleanSetting,
    val list: ListSettings<Int>
) : SettingGroup("my group") // имя по желанию, если не указывать будет использоваться название класса

class Example : Parent(
    name = "example",
    description = "example module",
    category = Categories.example,
    enabledByDefault = false,
    disableOnStartup = false,
    defaultKeyBind = GLFW.GLFW_KEY_R
) {
    val bool by BooleanSetting("boolean", false)
    val list by ListSettings("list", 1, 2, 3)

    val group = MyGroup(
        BooleanSetting("boolean 2", true),
        ListSettings("list 2", 1, 2, 3)
    )

    fun test() {
        println(group.bool.value)
    }
}