package android.view

import android.content.Context

@Suppress("UNUSED_PARAMETER")
class ViewConfiguration {
    companion object {
        @JvmStatic
        fun get(context: Context) = ViewConfiguration()
    }
}