package io.serge2nd.taskhero.etc

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import kotlin.reflect.KClass

@Constraint(validatedBy = [])
@Pattern(regexp = "PT(\\d+H)?(\\d+M)?", message = "must be an ISO 8601 duration in hours and/or minutes")
annotation class Iso8601HoursMinutes(
    val message: String = "must be an ISO 8601 duration in hours and/or minutes",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
