package io.github.mdaman45.queueforge.jobservice.exception;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
                MethodArgumentNotValidException ex
        ) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                        .getFieldErrors()
                        .forEach(error ->
                                errors.put(
                                        error.getField(),
                                        error.getDefaultMessage()
                                ));

                ApiResponse<Map<String, String>> response =
                        new ApiResponse<>(
                                false,
                                "Validation failed",
                                errors,
                                Instant.now()
                        );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(response);
        }




        

        @ExceptionHandler(InvalidJobStateException.class)
        public ResponseEntity<ApiResponse<Void>> handleInvalidJobStateException(
                InvalidJobStateException ex
        ) {

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null,
                        Instant.now()
                );

        return ResponseEntity
                .badRequest()
                .body(response);
        }
}