package retrofit3

import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import org.jetbrains.annotations.NotNull
import java.lang.UnsupportedOperationException
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.Executor
import kotlin.AssertionError

abstract class Platform {
    companion object {
        fun get() = when (System.getProperty("java.vm.name")) {
            "Dalvik" -> if (Android24.isSupported()) Android24() else Android21()
            else -> Java8()
        }
    }

    class MainThreadExecutor : Executor {
        companion object {
            val INSTANCE = MainThreadExecutor()
        }

        private val handler: Handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            handler.post(command!!)
        }
    }

    class Android21 : Platform() {
        override fun defaultCallbackExecutor() = MainThreadExecutor.INSTANCE

        override fun createDefaultCallAdapterFactories(callbackExecutor: Executor): List<CallAdapter.Factory> =
            Collections.singletonList(DefaultCallAdapterFactory(callbackExecutor))

        override fun createDefaultConverterFactories(): List<Converter.Factory> =
            listOf()

        override fun isDefaultMethod(method: Method) = false

        override fun invokeDefaultMethod(
            method: Method,
            declaringClass: Class<*>,
            proxy: Any,
            vararg args: Any
        ): Any {
            throw AssertionError()
        }
    }

    @TargetApi(24)
    class Android24 : Platform() {
        companion object {
            fun isSupported() = Build.VERSION.SDK_INT >= 24
        }

        override fun defaultCallbackExecutor(): Executor = MainThreadExecutor.INSTANCE

        override fun createDefaultCallAdapterFactories(callbackExecutor: Executor): List<CallAdapter.Factory> =
            listOf(DefaultCallAdapterFactory(callbackExecutor))

        override fun createDefaultConverterFactories(): List<Converter.Factory> =
            listOf()

        override fun isDefaultMethod(method: Method): Boolean = method.isDefault

        @RequiresApi(Build.VERSION_CODES.O)
        override fun invokeDefaultMethod(
            method: Method,
            declaringClass: Class<*>,
            proxy: Any,
            vararg args: Any
        ): Any {
            if (Build.VERSION.SDK_INT < 26)
                UnsupportedOperationException("Calling default methods on API 24 and 25 is not supported")

            val declaredConstructor = MethodHandles.Lookup::class.java.getDeclaredConstructor(
                Class::class.java,
                Int::class.java
            )

            declaredConstructor.isAccessible = true

            return declaredConstructor.newInstance(declaringClass, -1)
                .unreflectSpecial(method, declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(args)
        }
    }

    class Java8 : Platform() {
        override fun defaultCallbackExecutor(): Executor {
            TODO("Not yet implemented")
        }

        override fun createDefaultCallAdapterFactories(callbackExecutor: Executor): List<CallAdapter.Factory> {
            TODO("Not yet implemented")
        }

        override fun createDefaultConverterFactories(): List<Converter.Factory> {
            TODO("Not yet implemented")
        }

        override fun isDefaultMethod(method: Method): Boolean {
            TODO("Not yet implemented")
        }

        override fun invokeDefaultMethod(
            method: Method,
            declaringClass: Class<*>,
            proxy: Any,
            vararg args: Any
        ): Any {
            TODO("Not yet implemented")
        }

    }

    @NotNull
    abstract fun defaultCallbackExecutor(): Executor

    abstract fun createDefaultCallAdapterFactories(@NotNull callbackExecutor: Executor): List<CallAdapter.Factory>

    abstract fun createDefaultConverterFactories(): List<Converter.Factory>

    abstract fun isDefaultMethod(method: Method): Boolean

    @NotNull
    abstract fun invokeDefaultMethod(
        method: Method,
        declaringClass: Class<*>,
        proxy: Any,
        vararg args: Any
    ): Any
}