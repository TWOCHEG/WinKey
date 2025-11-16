package twocheg.mod.modules

import twocheg.mod.Categories
import twocheg.mod.settings.*

class Example : Parent(
    name = "Example",
    description = "пример модуля",
    category = Categories.example,
    enabledByDefault = false,
    disableOnStartup = false,
    defaultKeyBind = KeyBind(45)
) {

}