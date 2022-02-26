package retrofit3

import java.lang.reflect.Method

class Invocation(private val method: Method, private val arguments: List<*>) {

    companion object {
        fun of(method: Method, arguments: List<*>) = Invocation(method, listOf(arguments))
    }

    fun method() = method

    fun arguments() = arguments

    override fun toString(): String = "${method.declaringClass.name} ${method.name} $arguments"
}