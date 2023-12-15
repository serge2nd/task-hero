package io.serge2nd.taskhero.api

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import io.serge2nd.taskhero.service.ServiceError
import io.serge2nd.taskhero.service.ServiceError.*
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity

/**
 * Wraps a service answer with specific HTTP status
 */
fun Either<ServiceError, Any>.toResponse(status: HttpStatus = OK) = when (this) {
    is Left -> ResponseEntity(value, when (value) {
        is BadRequest -> BAD_REQUEST
        is Conflict -> CONFLICT
        is NotFound -> NOT_FOUND
    })
    is Right -> when (value) {
        is Unit -> ResponseEntity<Any>(NO_CONTENT)
        else -> ResponseEntity(value, status)
    }
}
