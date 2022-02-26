package retrofit3

import org.jetbrains.annotations.NotNull

/** Exception for an unexpected, non-2xx HTTP response. */
class HttpException(private val response: Response<*>) : RuntimeException(getMessage(response)) {

    companion object {
        fun getMessage(response: Response<*>) = "HTTP ${response.code()} ${response.message()}"
    }

    /** HTTP status code. */
    fun code() = response.code()

    /** HTTP status message. */
    fun message() = response.message()

    /** The full HTTP response. This may be null if the exception was serialized. */
    @NotNull
    fun response() = response
}