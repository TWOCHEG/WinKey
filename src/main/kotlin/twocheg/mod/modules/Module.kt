package twocheg.mod.modules

interface Module {
    val name: String
    val description: String?
    var keybind: Int
    var enable: Boolean

    fun toggle()
    fun init()
}