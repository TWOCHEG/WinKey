package twocheg.mod.utils.math

import java.util.concurrent.ThreadLocalRandom

object MathUtils {
    fun random(min: Double, max: Double): Double {
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min
    }

    fun random(min: Float, max: Float): Float {
        return (Math.random() * (max - min) + min).toFloat()
    }

    fun random(min: Int, max: Int): Int {
        return (Math.random() * (max - min) + min).toInt()
    }

    fun splitText(
        text: String, maxWidth: Float,
        widthCalculator: (String) -> Float,
        splitRegex: String
    ): List<String> {
        val lines = mutableListOf<String>()

        for (paragraph in text.split("\\n".toRegex()).toTypedArray()) {
            var currentLine = StringBuilder()

            for (word in paragraph.trim().split(splitRegex.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val fits = if (currentLine.isEmpty()) widthCalculator(word) <= maxWidth
                else widthCalculator(currentLine.toString() + splitRegex + word) <= maxWidth

                if (!fits) {
                    if (!currentLine.isEmpty()) {
                        lines.add(currentLine.toString())
                        currentLine = StringBuilder()
                    }
                    if (widthCalculator(word) > maxWidth) {
                        for (c in word.toCharArray()) {
                            val charStr = c.toString()
                            if (widthCalculator(currentLine.toString() + charStr) > maxWidth && !currentLine.isEmpty()) {
                                lines.add(currentLine.toString())
                                currentLine = StringBuilder()
                            }
                            currentLine.append(c)
                        }
                    } else currentLine.append(word)
                } else {
                    if (!currentLine.isEmpty()) {
                        currentLine.append(splitRegex)
                    }
                    currentLine.append(word)
                }
            }
            if (!currentLine.isEmpty()) lines.add(currentLine.toString())
        }

        return lines
    }

    fun splitText(t: String, maxWidth: Float, widthCalculator: (String) -> Float): List<String> {
        return splitText(t, maxWidth, widthCalculator, " ")
    }
}