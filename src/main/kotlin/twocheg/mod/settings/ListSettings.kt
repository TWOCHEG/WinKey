package twocheg.mod.settings

class ListSettings<T>(
    override val name: String,
    vararg values: T
) : SettingBase<Int>(name, 0) {
    val values = values.toList()

    var current: T
        get() = values[value]
        set(v) {
            value = values.indexOf(v)
        }

    fun setIndex(i: Int) {
        value = i
    }

    fun next(): T {
        var i = value + 1
        if (i > values.size - 1) i = 0
        else if (i < 0) i = values.size - 1
        setIndex(i)
        return current
    }

    fun prev(): T {
        var i = value - 1
        if (i > values.size - 1) i = 0
        else if (i < 0) i = values.size - 1
        setIndex(i)
        return current
    }

    @Suppress("UNCHECKED_CAST")
    fun setAny(v: Any?) {
        current = v as T
    }
}
