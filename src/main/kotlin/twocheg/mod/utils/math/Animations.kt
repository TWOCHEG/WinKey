// представляю, новый, крутой, удобный, полезный инструмент, мечта любого дизайнера читов на майнкрафт (да да это чистая правда)
// автоматический расчитыватель значений 3000 инатор редми т ксяоми 21 про, сокращенно крутые классы для анимаций
package twocheg.mod.utils.math

import kotlin.math.*

data class EasingCurve(
    val name: String? = null,
    val interpolate: (t: Float) -> Float
) {
    companion object {
        val Linear = EasingCurve("Linear") { it }

        val EaseIn = EasingCurve("EaseIn") { t -> t * t }
        val EaseOut = EasingCurve("EaseOut") { t -> 1f - (1f - t).pow(2) }
        val EaseInOut = EasingCurve("EaseInOut") { t ->
            if (t < 0.5f) 4f * t * t * t else 1f - (-2f * t + 2f).pow(3f) / 2f
        }

        fun cubicBezier(x1: Float, y1: Float, x2: Float, y3: Float): EasingCurve =
            EasingCurve("cubic-bezier($x1, $y1, $x2, $y3)") { t ->
                cubicBezierInterpolate(t, x1, y1, x2, y3)
            }

        val Ease = cubicBezier(0.25f, 0.1f, 0.25f, 1.0f)
        val EaseInSine = cubicBezier(0.12f, 0f, 0.39f, 0f)
        val EaseOutSine = cubicBezier(0.61f, 1f, 0.88f, 1f)
        val EaseInOutSine = cubicBezier(0.37f, 0f, 0.63f, 1f)

        val BounceOut = EasingCurve("BounceOut") { t ->
            when {
                t < 1f / 2.75f -> 7.5625f * t * t
                t < 2f / 2.75f -> 7.5625f * (t - 1.5f / 2.75f).pow(2) + 0.75f
                t < 2.5f / 2.75f -> 7.5625f * (t - 2.25f / 2.75f).pow(2) + 0.9375f
                else -> 7.5625f * (t - 2.625f / 2.75f).pow(2) + 0.984375f
            }
        }
    }
}

private fun cubicBezierInterpolate(t: Float, x1: Float, y1: Float, x2: Float, y3: Float): Float {
    val cx = 3f * x1
    val bx = 3f * (x2 - x1) - cx
    val ax = 1f - cx - bx

    val cy = 3f * y1
    val by = 3f * (y3 - y1) - cy
    val ay = 1f - cy - by

    // решение уравнения: ax*t^3 + bx*t^2 + cx*t = input_t
    fun solveCurveX(t: Float): Float = ((ax * t + bx) * t + cx) * t

    // Ньютон-Рафсон для нахождения t по x
    var t0 = t
    repeat(6) {
        val x = solveCurveX(t0) - t
        if (x.absoluteValue < 1e-6f) return ((ay * t0 + by) * t0 + cy) * t0
        val d = (3f * ax * t0 + 2f * bx) * t0 + cx
        if (d.absoluteValue < 1e-6f) return ((ay * t0 + by) * t0 + cy) * t0
        t0 -= x / d
    }
    return ((ay * t0 + by) * t0 + cy) * t0
}

class Delta(
    private val direction: () -> Boolean,
    private val durationMs: Float = 400f,
    private val curve: EasingCurve = EasingCurve.EaseInOut
) {
    private val timer = Timer()
    private var virtualTimeMs: Float = 0f

    init {
        reset()
    }

    fun reset() {
        virtualTimeMs = 0f
        timer.reset()
    }

    fun setProgress(progress: Float) {
        virtualTimeMs = (progress.coerceIn(0f, 1f) * durationMs)
        timer.reset()
    }

    fun get(): Float {
        val deltaMs = timer.updateDeltaMs()

        val targetTime = if (direction()) durationMs else 0f

        virtualTimeMs = if (virtualTimeMs < targetTime) {
            minOf(virtualTimeMs + deltaMs, targetTime)
        } else {
            maxOf(virtualTimeMs - deltaMs, targetTime)
        }

        val t = (virtualTimeMs / durationMs).coerceIn(0f, 1f)
        return curve.interpolate(t)
    }
}

class Pulse(
    val direct: () -> Boolean,
    val durationMs: Long = 800,
    val parentFactor: () -> Float = { 1f }
) {
    private val timer = Timer()
    private var accumulatedTime: Float = 0f
    private var targetDirection: Boolean = true

    init {
        reset()
    }

    fun reset() {
        accumulatedTime = 0f
        timer.reset()
        targetDirection = direct()
    }

    fun get(): Float {
        val deltaMs = timer.updateDeltaMs()
        val desiredDirection = direct()

        if (desiredDirection != targetDirection) {
            targetDirection = desiredDirection
            timer.reset()
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
            val decay = (accumulatedTime / durationMs).coerceAtMost(1f)
            pulseValue * (1f - decay)
        }
    }

    private fun calculateSinePulseProgress(): Float {
        if (durationMs <= 0) return 0f
        val phase = (accumulatedTime % durationMs) / durationMs
        return sin(Math.PI * phase).toFloat().coerceIn(0f, 1f)
    }
}

class Spring(
    initialValue: Float,
    private val stiffness: Float = 300f,
    private val damping: Float = 25f,
    private val mass: Float = 1f
) {
    var current: Float = initialValue; private set
    var target: Float = initialValue; private set

    private var velocity: Float = 0f
    private val timer = Timer()

    init {
        forceSet(initialValue)
    }

    fun set(newTarget: Float) {
        target = newTarget
    }

    fun forceSet(value: Float) {
        target = value
        current = value
        velocity = 0f
        timer.reset()
    }

    fun get(): Float {
        val dt = timer.updateDeltaSec()
        if (dt <= 0f) return current

        val displacement = target - current
        val springForce = stiffness * displacement
        val dampingForce = damping * velocity
        val acceleration = (springForce - dampingForce) / mass

        velocity += acceleration * dt
        current += velocity * dt

        val eps = 1e-4f
        if (abs(displacement) < eps && abs(velocity) < eps) {
            current = target
            velocity = 0f
        }

        return current
    }
}

class Hybrid(
    initialValue: Float,
    durationMs: Float = 200f,
) {
    private val spring = Spring(initialValue, 300f, 25f)
    private val minDurationNs = (durationMs * 1e6f).toLong()
    private var lastSetTime = 0L

    fun set(newTarget: Float) {
        val now = System.nanoTime()
        if (now - lastSetTime > minDurationNs) {
            spring.forceSet(spring.current)
        }
        spring.set(newTarget)
        lastSetTime = now
    }

    fun get(): Float = spring.get()
    fun forceSet(v: Float) = spring.forceSet(v)
}
