package io.serge2nd.taskhero.api

import io.github.oshai.kotlinlogging.KotlinLogging

import org.springframework.core.NestedRuntimeException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.apache.commons.lang3.StringUtils.EMPTY as na

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler
    fun handle(e: WebExchangeBindException) = e.bindingResult.fieldErrors.map {
        FormatError(it.field, it.rejectedValue, it.defaultMessage ?: "invalid")
    }.plus(e.bindingResult.globalErrors.map {
        object { val msg = it.defaultMessage }
    }).let { ResponseEntity(it, BAD_REQUEST) }

    @ExceptionHandler
    fun handle(e: NestedRuntimeException) = e.rootCause?.let(::handleRoot) ?: handle(e as Throwable)

    @ExceptionHandler
    fun handle(e: Throwable) = ResponseEntity<Any>(
        object { val msg = "$e" },
        INTERNAL_SERVER_ERROR
    ).also { log.error(e, ::na) }

    private fun handleRoot(e: Throwable) = when (e) {
        is WebExchangeBindException -> handle(e)
        else -> handle(e)
    }

    data class FormatError(val prop: String, val value: Any?, val msg: String)
}

private val log = KotlinLogging.logger {}
