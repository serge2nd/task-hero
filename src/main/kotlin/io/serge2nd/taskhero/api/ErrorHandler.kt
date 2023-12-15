package io.serge2nd.taskhero.api

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler
    fun handle(e: WebExchangeBindException) = e.bindingResult.fieldErrors.map {
        FormatError(it.field, it.rejectedValue?.toString(),it.defaultMessage ?: "invalid")
    }.plus(e.bindingResult.globalErrors.map {
        object { val msg = it.defaultMessage }
    }).let { ResponseEntity(it, BAD_REQUEST) }

    @ExceptionHandler
    fun handle(e: ServerWebInputException) = ResponseEntity(
        FormatError(e.methodParameter?.parameterName, null, e.message),
        BAD_REQUEST
    )

    @ExceptionHandler
    fun handle(e: Throwable) = ResponseEntity<Any>(
        object { val stackTrace = e.stackTraceToString() },
        INTERNAL_SERVER_ERROR
    )

    class FormatError(val prop: String?, val value: String?, val msg: String)
}
