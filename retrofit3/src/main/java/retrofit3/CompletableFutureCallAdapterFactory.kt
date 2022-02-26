package retrofit3

import android.annotation.TargetApi
import java.lang.reflect.Type

@TargetApi(24)
class CompletableFutureCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>): CallAdapter<*, *>? {
        TODO("Not yet implemented")
    }
}