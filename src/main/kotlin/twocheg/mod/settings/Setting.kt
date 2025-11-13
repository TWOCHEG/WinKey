package twocheg.mod.settings

import twocheg.mod.managers.ConfigManager
import twocheg.mod.utils.*

open class Setting<T>(
    open val name: String,
    open val defaultValue: T,
    val min: T? = null,
    val max: T? = null,
    private val options: List<T>? = null,
    private val visibility: (T) -> Boolean = { true },
    private val onChange: (T) -> Unit = {}
) {
    var parentGroup: Setting<*>? = null
        internal set

    var config: ConfigManager? = null

    private var optionIndex: Int = options?.indexOf(defaultValue) ?: 0

    private var value: T? = if (options == null) defaultValue else null

    init {
        require(options == null || options.contains(defaultValue)) {
            "default value must be in options list"
        }
    }

    val isList: Boolean get() = options != null
    val isNum: Boolean get() = min != null && max != null
    val isBoolean: Boolean get() = defaultValue is Boolean
    val isString: Boolean get() = defaultValue is String

    fun getValue(): T {
        return if (isList) {
            options!![optionIndex.coerceIn(0 until options.size)]
        } else {
            value ?: defaultValue
        }
    }

    fun getPow2Value(): Float {
        return when (val v = getValue()) {
            is Float -> v * v
            is Int -> (v * v).toFloat()
            else -> 0f
        }
    }

    fun setValue(newValue: T) {
        if (config != null && newValue != getValue()) {
            if (isList) {
                val newIndex = options!!.indexOf(newValue).takeUnless { it == -1 } ?: return
                optionIndex = newIndex
                config!!.set(name, newIndex)
            } else {
                value = newValue
                config!!.set(name, newValue)
            }

            onChange(getValue())
        }
    }

    fun getIndex(): Int = optionIndex

    fun setIndex(newIndex: Int) {
        if (!isList) return
        val clamped = newIndex.coerceIn(0 until options!!.size)
        setValue(options[clamped])
    }

    fun getOptions(): List<T>? = options

    fun init(config: ConfigManager) {
        this.config = config
        if (parentGroup != null) {
            config.keyPath += ".${(parentGroup as Setting<*>).name}"
        }
        val savedValue = config.get(name, if (isList) optionIndex else defaultValue)

        if (isList) {
            @Suppress("UNCHECKED_CAST")
            setIndex(savedValue as Int)
        } else {
            @Suppress("UNCHECKED_CAST")
            setValue(savedValue as T)
        }
    }

    fun resetToDefault() {
        setValue(defaultValue)
    }

    fun isVisible(): Boolean {
        return visibility(getValue())
    }

    fun getGroup(): Setting<*>? {
        return parentGroup
    }

    companion object {
        @JvmStatic
        inline fun <reified E : Enum<E>> fromEnum(defaultValue: E): Setting<E> {
            val name = humanizeEnumClassName(defaultValue::class.java)
            val options = enumEntries<E>()
            return Setting(name, defaultValue, options = options)
        }

        @JvmStatic
        fun <T> fromOptions(defaultValue: T, name: String, vararg options: T): Setting<T> {
            return Setting(name, defaultValue, options = options.toList())
        }

        @JvmStatic
        fun <T> fromRange(name: String, defaultValue: T, min: T, max: T): Setting<T> {
            return Setting(name, defaultValue, min = min, max = max)
        }
    }
}