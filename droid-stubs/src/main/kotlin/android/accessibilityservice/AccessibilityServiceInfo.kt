package android.accessibilityservice

class AccessibilityServiceInfo {
    @JvmField
    var flags: Int = 0

    companion object {
        const val FLAG_RETRIEVE_INTERACTIVE_WINDOWS = 1
    }
}