package retrofit3

import org.jetbrains.annotations.Nullable
import java.lang.reflect.Method

abstract class ServiceMethod<T> {

    companion object {
        fun <T> parseAnnotations(retrofit3: Retrofit3, method: Method): ServiceMethod<T> {
            val requestFactory = RequestFactory.parseAnnotations(retrofit3, method)

            val returnType = method.genericReturnType
            if (Utils.hasUnresolvableType(returnType)) {
                throw Utils.methodError(
                    method,
                    "Method return type must not include a type variable or wildcard: %s",
                    returnType
                )
            }

            if (Utils.isVoid(returnType)) {
                throw Utils.methodError(method, "Service methods cannot return void.")
            }

            TODO()

//            return HttpServiceMethod.parseAnnotations(retrofit3, method, requestFactory)
        }
    }

    @Nullable
    abstract fun invoke(args: Array<Any>): T

}