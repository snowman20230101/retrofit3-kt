package retrofit3

import okhttp3.Request
import okio.Timeout
import org.jetbrains.annotations.NotNull
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

class DefaultCallAdapterFactory(@NotNull val callBackExecutor: Executor) : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        if (!(returnType is ParameterizedType)) {
            throw IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>")
        }

        val responseType = Utils.getParameterUpperBound(0, returnType as ParameterizedType?)

        val executor: Executor? = if (!Utils.isAnnotationPresent(
                annotations,
                SkipCallbackExecutor::class.java
            )
        ) callBackExecutor else null

        return DefaultCallAdapter(executor, responseType)
    }

    class DefaultCallAdapter(
        private val callbackExecutor: Executor?,
        private val responseType: Type
    ) : CallAdapter<Any, Call<*>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<Any>): Call<*> {
            return if (callbackExecutor == null) call else ExecutorCallbackCall(
                callbackExecutor,
                call
            )
        }
    }

    class ExecutorCallbackCall<T>(
        private val callbackExecutor: Executor?,
        private val delegate: Call<T>?
    ) : Call<T> {

        override fun execute(): Response<T> = delegate?.execute()!!

        override fun enqueue(@NotNull callback: Callback<T>) {
            delegate?.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callbackExecutor?.execute {
                        if (delegate.isCanceled()) callback.onFailure(
                            this@ExecutorCallbackCall,
                            IOException("Canceled")
                        ) else callback.onResponse(this@ExecutorCallbackCall, response)
                    }
                }

                override fun onFailure(call: Call<T>, thr: Throwable) {
                    callbackExecutor?.execute {
                        callback.onFailure(this@ExecutorCallbackCall, thr)
                    }
                }
            })
        }

        override fun isExecuted(): Boolean = delegate?.isExecuted()!!

        override fun cancel(): Unit = delegate?.cancel()!!

        override fun isCanceled(): Boolean {
            return delegate?.isCanceled()!!
        }

        @SuppressWarnings("CloneDoesn'tCallSuperClone") // Performing deep clone.
        override fun clone(): Call<T> = ExecutorCallbackCall(callbackExecutor, delegate?.clone())

        override fun request(): Request = delegate?.request()!!

        override fun timeout(): Timeout = delegate?.timeout()!!
    }
}