package retrofit3

import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.IOException
import org.jetbrains.annotations.Nullable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Convert objects to and from their representation in HTTP. Instances are created by {@linkplain
 * Factory a factory} which is {@linkplain Retrofit.Builder#addConverterFactory(Factory) installed}
 * into the {@link Retrofit} instance.
 */
interface Converter<F, T> {

    /**
     *
     */
    @Nullable
    @Throws(IOException::class)
    fun convert(value: F): T

    abstract class Factory {

        /**
         * Returns a {@link Converter} for converting an HTTP response body to {@code type}, or null if
         * {@code type} cannot be handled by this factory. This is used to create converters for
         * response types such as {@code SimpleResponse} from a {@code Call<SimpleResponse>}
         * declaration.
         */
        @Nullable
        open fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit3: Retrofit3
        ): Converter<ResponseBody, *>? {
            TODO()
        }

        /**
         * Returns a {@link Converter} for converting {@code type} to an HTTP request body, or null if
         * {@code type} cannot be handled by this factory. This is used to create converters for types
         * specified by {@link Body @Body}, {@link Part @Part}, and {@link PartMap @PartMap} values.
         */
        @Nullable
        open fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit3: Retrofit3
        ): Converter<*, RequestBody>? {
            TODO()
        }

        /**
         * Returns a {@link Converter} for converting {@code type} to a {@link String}, or null if
         * {@code type} cannot be handled by this factory. This is used to create converters for types
         * specified by {@link Field @Field}, {@link FieldMap @FieldMap} values, {@link Header @Header},
         * {@link HeaderMap @HeaderMap}, {@link Path @Path}, {@link Query @Query}, and {@link
         * QueryMap @QueryMap} values.
         */
        @Nullable
        open fun stringConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit3: Retrofit3
        ): Converter<*, String>? {
            TODO()
        }

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