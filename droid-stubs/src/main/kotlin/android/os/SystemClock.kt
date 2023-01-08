package android.os

import java.util.concurrent.atomic.AtomicLong

class SystemClock {
    companion object {
        private val timeDelta = AtomicLong()

        @JvmStatic
        fun uptimeMillis() = System.currentTimeMillis() + timeDelta.get()

        @JvmStatic
        fun sleep(delay: Long) {
            // emulate sleeping without actually delaying
            timeDelta.addAndGet(delay)
        }
    }
}