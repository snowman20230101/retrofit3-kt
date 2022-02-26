package retrofit3

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Adapts a {@link Call} with response type {@code R} into the type of {@code T}. Instances are
 * created by {@linkplain Factory a factory} which is {@linkplain
 * Retrofit.Builder#addCallAdapterFactory(Factory) installed} into the {@link Retrofit} instance.
 */
interface CallAdapter<R, T> {

    /**
     * Returns the value type that this adapter uses when converting the HTTP response body to a Java
     * object. For example, the response type for {@code Call<Repo>} is {@code Repo}. This type is
     * used to prepare the {@code call} passed to {@code #adapt}.
     *
     * <p>Note: This is typically not the same type as the {@code returnType} provided to this call
     * adapter's factory.
     */
    fun responseType(): Type

    /**
     * Returns an instance of {@code T} which delegates to {@code call}.
     *
     * <p>For example, given an instance for a hypothetical utility, {@code Async}, this instance
     * would return a new {@code Async<R>} which invoked {@code call} when run.
     *
     * <pre><code>
     * &#64;Override
     * public &lt;R&gt; Async&lt;R&gt; adapt(final Call&lt;R&gt; call) {
     *   return Async.create(new Callable&lt;Response&lt;R&gt;&gt;() {
     *     &#64;Override
     *     public Response&lt;R&gt; call() throws Exception {
     *       return call.execute();
     *     }
     *   });
     * }
     * </code></pre>
     */
    fun adapt(call: Call<R>): T

    /**
     * Creates {@link CallAdapter} instances based on the return type of {@linkplain
     * Retrofit#create(Class) the service interface} methods.
     */
    abstract class Factory {
        abstract fun get(returnType: Type, annotations: Array<Annotation>): CallAdapter<*, *>?

        companion object {

            /**
             * Extract the upper bound of the generic parameter at {@code index} from {@code type}. For
             * example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
             */
            fun getParameterUpperBound(index: Int, type: ParameterizedType): Type =
                Utils.getParameterUpperBound(index, type)

            /**
             * Extract the raw class type from {@code type}. For example, the type representing {@code
             * List<? extends Runnable>} returns {@code List.class}.
             */
            fun getRawType(type: Type): Class<*> = Utils.getRawType(type)

        }
    }
}