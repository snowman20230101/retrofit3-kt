package retrofit3

class Response<T> constructor(
    private val rawResponse: okhttp3.Response,
    private val body: T,
    private val errorBody: okhttp3.RequestBody
) {

    /** The raw response from the HTTP client. */
    fun raw() = rawResponse

    /** HTTP status code. */
    fun code() = rawResponse.code

    /** HTTP status message or null if unknown. */
    fun message() = rawResponse.message

    /** Returns true if {@link #code()} is in the range [200..300). */
    fun isSuccessful() = rawResponse.isSuccessful

    /** HTTP headers. */
    fun headers() = rawResponse.headers

    /** The deserialized response body of a {@linkplain #isSuccessful() successful} response. */
    fun body() = body

    /** The raw response body of an {@linkplain #isSuccessful() unsuccessful} response. */
    fun errorBody() = errorBody

    override fun toString() = rawResponse.toString()
}