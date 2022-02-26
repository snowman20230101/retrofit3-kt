package retrofit3

import okio.IOException

abstract class ParameterHandler<T> {

    @Throws(IOException::class)
    abstract fun apply(requestBuilder: RequestBuilder, value: T): Unit
}
