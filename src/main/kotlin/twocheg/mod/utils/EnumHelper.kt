package twocheg.mod.utils

fun humanizeEnumClassName(enumClass: Class<*>): String {
    return enumClass.simpleName
        .replace(Regex("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"), " ")
        .split(" ")
        .joinToString(" ") { it.lowercase() }
        .trim()
        .let { if (it.endsWith("s", ignoreCase = true)) it.dropLast(1) else it }
}

inline fun <reified E : Enum<E>> enumEntries(): List<E> {
    return enumValues<E>().toList()
}

fun <E : Enum<E>> E.humanizedName(): String {
    return humanizeEnumClassName(this::class.java)
}