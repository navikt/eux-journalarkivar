package no.nav.eux.journalarkivar.advice

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@RestControllerAdvice
class MethodArgumentNotValidExceptionAdvice {

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentValidationExceptions(
        exception: MethodArgumentNotValidException,
        webRequest: WebRequest
    ) = ResponseEntity
        .status(BAD_REQUEST)
        .body(exception.apiError)

    val MethodArgumentNotValidException.apiError
        get() = ApiError(errors = errors)

    val MethodArgumentNotValidException.errors
        get() = bindingResult
            .fieldErrors
            .map { it.error }
            .sortedBy { it.defaultMessage }
            .sortedBy { it.field }

    val FieldError.error
        get() = Error(
            field = field,
            defaultMessage = defaultMessage,
            rejectedValue = rejectedValue
        )

    data class ApiError(
        val timestamp: LocalDateTime = now(),
        val errors: List<Error>
    )

    data class Error(
        val field: String,
        val defaultMessage: String?,
        val rejectedValue: Any?,
    )
}
