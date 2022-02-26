package retrofit3

import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.IOException
import retrofit3.http.Streaming
import java.lang.reflect.Type

class BuiltInConverters : Converter.Factory() {
    /** Not volatile because we don't mind multiple threads discovering this. */
    var checkForKotlinUnit = false

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit3: Retrofit3
    ): Converter<*, RequestBody>? {

        if (RequestBody::class.java.isAssignableFrom(Utils.getRawType(type))) {
            return RequestBodyConverter.INSTANCE
        }

        return null
    }

    /**
     * TODO 这里的代码可以优吗？ 可以用kotlin的简洁特性吗
     */
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit3: Retrofit3
    ): Converter<ResponseBody, *>? {

        when (type) {
            ResponseBody::class.java -> if (Utils.isAnnotationPresent(
                    annotations,
                    Streaming::class.java
                )
            ) StreamingResponseBodyConverter.INSTANCE else BufferingResponseBodyConverter.INSTANCE
            Void::class.java -> VoidResponseBodyConverter.INSTANCE
            Unit::class.java -> UnitResponseBodyConverter.INSTANCE
        }
        return null
    }

    class VoidResponseBodyConverter : Converter<ResponseBody, Void?> {
        companion object {
            val INSTANCE = VoidResponseBodyConverter()
        }

        override fun convert(value: ResponseBody): Void? {
            value.close()
            return null
        }
    }

    class UnitResponseBodyConverter : Converter<ResponseBody, Unit> {
        companion object {
            val INSTANCE = UnitResponseBodyConverter()
        }

        override fun convert(value: ResponseBody) = kotlin.run { value.close() }
    }

    class RequestBodyConverter : Converter<RequestBody, RequestBody> {
        companion object {
            val INSTANCE = RequestBodyConverter()
        }

        override fun convert(value: RequestBody): RequestBody = kotlin.run { value }
    }

    class StreamingResponseBodyConverter : Converter<ResponseBody, ResponseBody> {
        companion object {
            val INSTANCE = StreamingResponseBodyConverter()
        }

        override fun convert(value: ResponseBody): ResponseBody = kotlin.run { value }
    }

    class BufferingResponseBodyConverter : Converter<ResponseBody, ResponseBody> {
        companion object {
            val INSTANCE = BufferingResponseBodyConverter()
        }

        override fun convert(value: ResponseBody): ResponseBody {
            try {
                // Buffer the entire body to avoid future I/O.
                return Utils.buffer(value)
            } catch (e: IOException) {

            }
            throw okio.IOException("");
        }
    }

    class ToStringConverter : Converter<Any, String> {
        companion object {
            val INSTANCE = ToStringConverter()
        }

        override fun convert(value: Any): String = kotlin.run { value.toString() }
    }
}