package twocheg.mod.utils.math


class Timer {
    private var time: Long = 0

    init {
        reset()
    }

    fun passedS(s: Double): Boolean {
        return getMs(System.nanoTime() - time) >= (s * 1000.0).toLong()
    }

    fun passedMs(ms: Long): Boolean {
        return getMs(System.nanoTime() - time) >= ms
    }

    fun every(ms: Long): Boolean {
        val passed = getMs(System.nanoTime() - time) >= ms
        if (passed) reset()
        return passed
    }

    fun setMs(ms: Long) {
        this.time = System.nanoTime() - ms * 1000000L
    }

    val passedTimeMs: Long
        get() = getMs(System.nanoTime() - time)

    fun reset() {
        this.time = System.nanoTime()
    }

    fun getMs(time: Long): Long {
        return time / 1000000L
    }

    val timeMs: Long
        get() = getMs(System.nanoTime() - time)
}