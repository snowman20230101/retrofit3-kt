package retrofit3

import okhttp3.ResponseBody
import java.lang.reflect.Method

abstract class HttpServiceMethod<ResponseT, ReturnT>(
    private val requestFactory: RequestFactory,
    private val callFactory: okhttp3.Call.Factory,
    private val responseConverter: Converter<ResponseBody, ResponseT>
) : ServiceMethod<ReturnT>() {

    companion object {
        fun <ResponseT, ReturnT> parseAnnotations(
            retrofit3: Retrofit3,
            method: Method,
            requestFactory: RequestFactory
        ): HttpServiceMethod<ResponseT, ReturnT> {
            TODO()
        }
    }

    override fun invoke(args: Array<Any>): ReturnT = adapt(
        OkHttpCall(
            requestFactory = requestFactory,
            callFactory = callFactory,
            args = args,
            responseConverter = responseConverter
        ), args
    )

    abstract fun adapt(call: Call<ResponseT>, args: Array<Any>): ReturnT
}