package io.serge2nd.taskhero.etc

import io.serge2nd.taskhero.etc.NullMethod.none
import org.springframework.core.MethodParameter
import org.springframework.validation.MapBindingResult
import org.springframework.web.bind.support.WebExchangeBindException
import javax.annotation.Nonnull
import javax.annotation.meta.TypeQualifierNickname
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KProperty1

/**
 * Allows to omit `field:` prefix on marking query params for API docs
 */
@Target(FIELD)
@Nonnull
@TypeQualifierNickname
annotation class NonNull

/**
 * Helps to fire a pretty error when a required param is null
 * @throws WebExchangeBindException
 */
fun <T> none(prop: KProperty1<*, T>): T = throw WebExchangeBindException(
    MethodParameter(none, -1),
    MapBindingResult(emptyMap<T, T>(), "").apply {
        rejectValue(prop.name, "", "must not be null")
    }
)

private object NullMethod { val none = javaClass.declaredConstructors[0] }
