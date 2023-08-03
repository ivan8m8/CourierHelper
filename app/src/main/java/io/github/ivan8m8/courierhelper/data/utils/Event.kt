package io.github.ivan8m8.courierhelper.data.utils

// Should be deprecated later.
// See https://developer.android.com/topic/architecture/ui-layer/events.

class Event<out T>(
    private val content: T
) {

    private var hasBeenHandled = false

    fun peekContent(): T = content

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other == null) return false

        other as Event<*>
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }
}