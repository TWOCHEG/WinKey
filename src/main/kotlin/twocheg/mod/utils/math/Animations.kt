// представляю, новый, крутой, удобный, полезный инструмент, мечта любого дизайнера читов на майнкрафт (да да это чистая правда)
// автоматический расчитыватель значений 3000 инатор редми т ксяоми 21 про, сокращенно крутые классы для анимаций
package twocheg.mod.utils.math

import kotlin.math.*

enum class AnimType {
    EaseIn,
    EaseOut,
    EaseInOut,
    Linear;

    fun get(t: Float): Float {
        return when (this) {
            Linear -> t
            EaseIn -> t * t
            EaseOut -> 1f - (1f - t) * (1f - t)
            EaseInOut -> {
                if (t < 0.5f) {
                    4f * t * t * t
                } else {
                    1f - (-2f * t + 2f).pow(3f) / 2f
                }
            }
        }
    }
}

class Delta(
    val direct: () -> Boolean,
    val durationMs: Long = 400,
    val mode: AnimType = AnimType.EaseInOut,
    val parentFactor: () -> Float = { 1f }
) {
    private var lastUpdateTime: Long = 0
    private var accumulatedTime: Float = 0f
    private var targetDirection: Boolean = true

    init {
        reset()
    }

    fun reset() {
        accumulatedTime = 0f
        lastUpdateTime = System.nanoTime()
        targetDirection = direct()
    }

    fun setProgress(progress: Float) {
        accumulatedTime = (progress.coerceIn(0f, 1f) * durationMs).coerceIn(0f, durationMs.toFloat())
        lastUpdateTime = System.nanoTime()
        targetDirection = direct()
    }

    fun get(): Float {
        val now = System.nanoTime()
        val deltaMs = (now - lastUpdateTime) / 1_000_000f
        lastUpdateTime = now

        accumulatedTime += if (targetDirection) deltaMs else -deltaMs
        accumulatedTime = accumulatedTime.coerceIn(0f, durationMs.toFloat())
        checkDirectionChange()
        return calculateProgress() * parentFactor()
    }

    private fun checkDirectionChange() {
        val desiredDirection = direct()
        if (desiredDirection != targetDirection) {
            targetDirection = desiredDirection
            lastUpdateTime = System.nanoTime()
        }
    }

    private fun calculateProgress(): Float {
        if (durationMs <= 0) return 1f
        val linearProgress = accumulatedTime / durationMs
        return mode.get(linearProgress).coerceIn(0f, 1f)
    }
}

class Pulse(
    val direct: () -> Boolean,
    val durationMs: Long = 400,
    val parentFactor: () -> Float = { 1f }
) {
    private var accumulatedTime: Float = 0f
    private var lastUpdateTime: Long = 0
    private var targetDirection: Boolean = true

    init {
        reset()
    }

    fun reset() {
        accumulatedTime = 0f
        lastUpdateTime = System.nanoTime()
        targetDirection = direct()
    }

    fun get(): Float {
        val now = System.nanoTime()
        val deltaMs = (now - lastUpdateTime) / 1_000_000f
        lastUpdateTime = now

        val desiredDirection = direct()

        if (desiredDirection != targetDirection) {
            targetDirection = desiredDirection
            lastUpdateTime = now
        }

        if (targetDirection) {
            accumulatedTime += deltaMs
        } else {
            val decayFactor = 1f - (deltaMs / durationMs).coerceAtMost(1f)
            accumulatedTime = (accumulatedTime * decayFactor).coerceAtLeast(0f)
        }

        return calculateProgress() * parentFactor()
    }

    private fun calculateProgress(): Float {
        return if (targetDirection) {
            calculateSinePulseProgress()
        } else {
            val pulseValue = calculateSinePulseProgress()
            val decay = accumulatedTime / durationMs
            pulseValue * (1f - decay)
        }
    }

    private fun calculateSinePulseProgress(): Float {
        if (durationMs <= 0) return 0f
        val phase = (accumulatedTime % durationMs) / durationMs
        val angle = Math.PI * phase
        return sin(angle).toFloat().coerceIn(0f, 1f)
    }
}

class Lerp(
    initialValue: Float,
    durationMs: Long = 400,
    mode: AnimType = AnimType.EaseInOut
) {
    var startValue = initialValue
    var currentValue = initialValue
    var targetValue = initialValue
    private val delta = Delta({ true }, durationMs, mode)

    fun set(newTarget: Float) {
        if (newTarget == targetValue) return
        startValue = currentValue
        targetValue = newTarget
        delta.reset()
    }

    fun forceSet(newTarget: Float) {
        startValue = newTarget
        currentValue = newTarget
        targetValue = newTarget
    }

    fun get(): Float {
        currentValue = lerp(startValue, targetValue, delta.get())
        return currentValue
    }

    private fun lerp(start: Float, end: Float, t: Float): Float {
        return start + (end - start) * t
    }
}
