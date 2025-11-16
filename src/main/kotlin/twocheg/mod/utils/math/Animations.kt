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
    private val curve: EasingCurve = EasingCurve.EaseInOut,
    private val parentFactor: () -> Float = { 1f }
) {
    private val timer = Timer()
    private var accumulatedMs: Float = 0f
    private var lastDirection: Boolean = direction()

    init {
        reset()
    }

    fun reset() {
        timer.reset()
        accumulatedMs = 0f
        lastDirection = direction()
    }

    fun setProgress(progress: Float) {
        accumulatedMs = (progress.coerceIn(0f, 1f) * durationMs).coerceIn(0f, durationMs)
        timer.setElapsedMs(0f)
        lastDirection = direction()
    }

    fun get(): Float {
        val deltaMs = timer.elapsedMs()
        timer.reset()

        val currentDir = direction()
        if (currentDir != lastDirection) {
            lastDirection = currentDir
            timer.reset()
            return getCurrentProgress()
        }

        accumulatedMs += if (currentDir) deltaMs else -deltaMs
        accumulatedMs = accumulatedMs.coerceIn(0f, durationMs)

        return getCurrentProgress()
    }

    private fun getCurrentProgress(): Float {
        val t = (accumulatedMs / durationMs).coerceIn(0f, 1f)
        return curve.interpolate(t) * parentFactor()
    }
}

class Pulse(
    private val direct: () -> Boolean,
    private val durationMs: Float = 800f,
    private val parentFactor: () -> Float = { 1f }
) {
    private val timer = Timer()
    private var accumulatedMs: Float = 0f
    private var lastDirection: Boolean = direct()

    init {
        reset()
    }

    fun reset() {
        timer.reset()
        accumulatedMs = 0f
        lastDirection = direct()
    }

    fun get(): Float {
        val deltaMs = timer.elapsedMs()
        timer.reset()

        val currentDir = direct()

        if (currentDir != lastDirection) {
            lastDirection = currentDir
            timer.reset()
        }

        if (currentDir) {
            accumulatedMs += deltaMs
        } else {
            val decaySpeed = deltaMs / durationMs
            accumulatedMs = (accumulatedMs * (1f - decaySpeed)).coerceAtLeast(0f)
        }

        return calculatePulse() * parentFactor()
    }

    private fun calculatePulse(): Float {
        if (accumulatedMs <= 0f) return 0f

        val phase = (accumulatedMs % durationMs) / durationMs
        val sine = sin(Math.PI * phase).toFloat().coerceIn(0f, 1f)

        val amplitude = (accumulatedMs / durationMs) - (accumulatedMs / durationMs).toInt()

        return sine * amplitude
    }
}

class Spring(
    initialValue: Float,
    private val stiffness: Float = 300f,
    private val damping: Float = 25f,
    private val mass: Float = 1f
) {
    private val timer = Timer()
    private var velocity: Float = 0f

    var current: Float = initialValue
        private set
    var target: Float = initialValue
        private set

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
        val dt = timer.deltaTimeSec()
        if (dt <= 0f) return current

        update(dt)
        return current
    }

    private fun update(dt: Float) {
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
    }
}

class Hybrid(
    initialValue: Float,
    durationMs: Float = 200f,
) {
    private val spring = Spring(initialValue, 300f, 25f)
    private val minDurationTimer = Timer()
    private val minDurationNs = (durationMs * 1e6f).toLong()

    fun set(newTarget: Float) {
        if (minDurationTimer.elapsedNs() > minDurationNs) {
            spring.forceSet(spring.current)
            minDurationTimer.reset()
        }
        spring.set(newTarget)
    }

    fun get(): Float = spring.get()

    fun forceSet(v: Float) {
        spring.forceSet(v)
        minDurationTimer.reset()
    }
}