package retrofit3

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jetbrains.annotations.NotNull
import java.lang.reflect.*
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor

class Retrofit3 internal constructor(
    private val callFactory: okhttp3.Call.Factory,
    private val baseUrl: HttpUrl,
    private val converterFactories: List<Converter.Factory>,
    private val defaultConverterFactoriesSize: Int,
    private val callAdapterFactories: List<CallAdapter.Factory>,
    private val defaultCallAdapterFactoriesSize: Int,
    @NotNull val callbackExecutor: Executor,
    val validateEagerly: Boolean = false
) {

    private val serviceMethodCache = ConcurrentHashMap<Method, ServiceMethod<*>>()

    fun <T> create(service: Class<T>): T {
        // check service
        validateServiceInterface(service)

        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<T>>(service),
            object : InvocationHandler {
                override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                    if (method == null) {
                        throw IllegalArgumentException("Proxy called invoke. but method == null")
                    }

                    if (method.declaringClass == Object::class.java) {
                        return method.invoke(this, args)
                    }

                    val platform = Platform.get()

                    return if (platform.isDefaultMethod(method)) platform.invokeDefaultMethod(
                        method,
                        service,
                        proxy!!,
                        args!!
                    ) else loadServiceMethod(method)
                }

            }) as T
    }

    /**
     *
     */
    private fun validateServiceInterface(service: Class<*>) {
        if (!service.isInterface)
            throw IllegalArgumentException("API declarations must be interfaces.")

        val check = ArrayDeque<Class<*>>(1)
        check.add(service)

        while (!check.isEmpty()) {
            val candidate = check.removeFirst()
            if (candidate.typeParameters.isNotEmpty()) {
                throw IllegalArgumentException("Type parameters are unsupported on ${candidate.name} which is an interface of ${service.name}")
            }

            // TODO *candidate.interfaces 为什么要加个*
            Collections.addAll(check, *candidate.interfaces)
        }

        if (validateEagerly) {
            val platform = Platform.get()
            for (method in service.declaredMethods) {
                // TODO 看不懂
                if (!platform.isDefaultMethod(method) && !Modifier.isStatic(method.modifiers)) {
                    loadServiceMethod(method)
                }
            }
        }
    }

    /**
     * TODO 泛型问题
     */
    private fun loadServiceMethod(method: Method): ServiceMethod<*>? {
        var result = serviceMethodCache[method]
        if (result != null) return result

        synchronized(serviceMethodCache) {
            if (result == null) {
//                result = ServiceMethod.parseAnnotations(this, method)
                result?.let { serviceMethodCache.put(method, it) }
            }
        }

        return result
    }


    fun callFactory() = callFactory

    fun baseUrl() = baseUrl

    fun callAdapterFactories() = callAdapterFactories

    fun callAdapter(type: Type, annotations: Array<Annotation>) =
        nextCallAdapter(null, type, annotations)

    fun nextCallAdapter(
        skipPast: CallAdapter.Factory?,
        type: Type,
        annotations: Array<Annotation>
    ): CallAdapter<*, *> {
        TODO()
    }

    fun converterFactories() = converterFactories

    fun <T> requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>
    ) = nextRequestBodyConverter<T>(null, type, parameterAnnotations, methodAnnotations)

    fun <T> nextRequestBodyConverter(
        skipPast: Converter.Factory?,
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>
    ): Converter<T, RequestBody> {
        TODO()
    }

    fun <T> responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>
    ) = nextResponseBodyConverter<T>(null, type, annotations)

    fun <T> nextResponseBodyConverter(
        skipPast: Converter.Factory?,
        type: Type,
        annotation: Array<Annotation>
    ): Converter<ResponseBody, T> {
        TODO()
    }

    fun <T> stringConverter(type: Type, annotations: Array<Annotation>): Converter<T, String> {
        TODO()
    }

    /**
     * The executor used for {@link Callback} methods on a {@link Call}. This may be {@code null}, in
     * which case callbacks should be made synchronously on the background thread.
     */
    @NotNull
    fun callbackExecutor() = callbackExecutor

    fun newBuilder() = Builder(this)

    class Builder constructor() {
        private lateinit var callFactory: okhttp3.Call.Factory
        private lateinit var baseUrl: HttpUrl
        private val converterFactories: List<Converter.Factory> = mutableListOf()
        private val callAdapterFactories: List<CallAdapter.Factory> = mutableListOf()
        private lateinit var callbackExecutor: Executor
        private var validateEagerly: Boolean = false

        constructor(retrofit3: Retrofit3) : this() {
            callFactory = retrofit3.callFactory
            baseUrl = retrofit3.baseUrl
            callbackExecutor = retrofit3.callbackExecutor
            validateEagerly = retrofit3.validateEagerly
        }

        fun client(client: OkHttpClient) = apply { callFactory(client) }

        fun callFactory(factory: okhttp3.Call.Factory) = apply { this.callFactory = factory }

        fun baseUrl(baseUrl: String) = apply { baseUrl(baseUrl.toHttpUrl()) }

        fun baseUrl(baseUrl: URL) = apply { baseUrl(baseUrl.toString()) }

        fun baseUrl(baseUrl: HttpUrl) = apply { this.baseUrl = baseUrl }


        fun build(): Retrofit3 {
            if (baseUrl == null) {
                throw IllegalStateException("Base URL required.")
            }

            val platform: Platform = Platform.get()

            var callFactory: okhttp3.Call.Factory = this.callFactory
            if (callFactory == null) {
                callFactory = OkHttpClient()
            }

            var callbackExecutor = this.callbackExecutor
            if (callbackExecutor == null) {
                callbackExecutor = platform.defaultCallbackExecutor()
            }

            val callAdapterFactories = mutableListOf(this.callAdapterFactories)
            val createDefaultCallAdapterFactories =
                platform.createDefaultCallAdapterFactories(callbackExecutor)
            callAdapterFactories.addAll(listOf(createDefaultCallAdapterFactories))



            return Retrofit3(
                callFactory,
                baseUrl,
                converterFactories,
                0,
                listOf(),
                0,
                callbackExecutor,
                validateEagerly
            )
        }
    }
}