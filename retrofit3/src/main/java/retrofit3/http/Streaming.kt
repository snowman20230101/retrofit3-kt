package retrofit3.http

/**
 * Treat the response body on methods returning {@link ResponseBody ResponseBody} as is, i.e.
 * without converting the body to {@code byte[]}.
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Streaming()
