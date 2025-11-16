package twocheg.mod.utils.math

class Timer {
    private var startTimeNs: Long = 0

    init {
        reset()
    }

    val passedTimeMs: Long
        get() = getMs(System.nanoTime() - startTimeNs)

    fun getMs(time: Long): Long {
        return time / 1000000L
    }

    fun reset() {
        startTimeNs = System.nanoTime()
    }

    val elapsedMs: Float
        get() = ((System.nanoTime() - startTimeNs) / 1_000_000f)

    val elapsedNs: Long
        get() = System.nanoTime() - startTimeNs


    fun setElapsedMs(ms: Float) {
        startTimeNs = System.nanoTime() - (ms * 1_000_000f).toLong()
    }

    fun passedMs(ms: Float): Boolean = elapsedMs >= ms
    fun passedS(s: Float): Boolean = passedMs(s * 1000f)

    fun everyMs(ms: Float): Boolean {
        if (passedMs(ms)) {
            reset()
            return true
        }
        return false
    }

    val deltaTimeSec: Float
        get() {
            val now = System.nanoTime()
            val deltaNs = now - startTimeNs
            startTimeNs = now
            return deltaNs / 1e9f
        }
}