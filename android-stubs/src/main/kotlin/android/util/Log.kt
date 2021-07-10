package android.util

import org.slf4j.LoggerFactory

class Log {
    companion object {
        private val logger = LoggerFactory.getLogger(Log::class.java)

        @JvmStatic
        fun isLoggable(a: String, b: Int): Boolean = true

        @JvmStatic
        fun d(tag: String, message: String): Int {
            logger.debug("$tag $message")
            return 0
        }

        @JvmStatic
        fun i(tag: String, message: String): Int {
            logger.info("$tag $message")
            return 0
        }
    }
}