package com.antony.openjobs.utils;

import com.antony.openjobs.common.api.ApiError;
import com.antony.openjobs.common.api.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(toApiError(exception)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        List<ApiError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiError(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(ApiResponse.failure(errors));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        String message = exception.getReason() != null
                ? exception.getReason()
                : "A requisição não pôde ser processada";

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ApiResponse.failure(new ApiError(null, message)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(new ApiError(null, "Dados já cadastrados")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(new ApiError(null, "Erro interno do servidor")));
    }

    private ApiError toApiError(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();

        if (cause instanceof InvalidFormatException invalidFormat) {
            return new ApiError(
                    getFieldName(invalidFormat),
                    "Tipo inválido: esperado %s, recebido %s".formatted(
                            getExpectedType(invalidFormat.getTargetType()),
                            getReceivedType(invalidFormat.getValue())
                    )
            );
        }

        if (cause instanceof MismatchedInputException mismatchedInput) {
            return new ApiError(
                    getFieldName(mismatchedInput),
                    "Tipo inválido: esperado %s".formatted(
                            getExpectedType(mismatchedInput.getTargetType())
                    )
            );
        }

        return new ApiError(null, "O corpo da requisição é inválido");
    }

    private String getFieldName(JacksonException exception) {
        List<JacksonException.Reference> path = exception.getPath();
        if (path.isEmpty()) {
            return null;
        }

        return path.get(path.size() - 1).getPropertyName();
    }

    private String getExpectedType(Class<?> targetType) {
        if (targetType == null) {
            return "valor válido";
        }
        if (targetType == String.class) {
            return "texto";
        }
        if (targetType == Integer.class || targetType == int.class
                || targetType == Long.class || targetType == long.class) {
            return "número inteiro";
        }
        if (Number.class.isAssignableFrom(targetType)
                || targetType == double.class || targetType == float.class) {
            return "número";
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return "booleano";
        }
        return "valor válido";
    }

    private String getReceivedType(Object value) {
        if (value instanceof String) {
            return "texto";
        }
        if (value instanceof Number) {
            return "número";
        }
        if (value instanceof Boolean) {
            return "booleano";
        }
        return "valor inválido";
    }
}
