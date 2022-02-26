package retrofit3

import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import org.jetbrains.annotations.Nullable

class OkHttpCall<T>(
    private val requestFactory: RequestFactory,
    private val args: Array<Any>,
    private val callFactory: okhttp3.Call.Factory,
    private val responseConverter: Converter<ResponseBody, T>
) : Call<T> {


    override fun execute(): Response<T> {
        TODO("Not yet implemented")

    }

    override fun enqueue(@Nullable callback: Callback<T>) {
        TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun isCanceled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun clone(): OkHttpCall<T> {
        return OkHttpCall<T>(requestFactory, args, callFactory, responseConverter)
    }

    override fun request(): Request {
        TODO("Not yet implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }
}