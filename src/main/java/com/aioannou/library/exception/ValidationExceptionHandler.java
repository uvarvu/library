package com.aioannou.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * An exception handler for validation errors.
 */
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationError(final MethodArgumentNotValidException e){
        final Map<String, String> validationErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
            .forEach(fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        e.getBindingResult().getGlobalErrors()
            .forEach(globalError -> validationErrors.put(globalError.getObjectName(), globalError.getDefaultMessage()));
        return validationErrors;
    }

    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<String> handleLibraryException(final LibraryException e) {
        return switch (e.getLibraryError()) {
            case BOOK_ALREADY_EXISTS -> ResponseEntity.status(HttpStatus.CONFLICT).body("Book already exists");
            case BOOK_DOES_NOT_EXIST, BOOK_UNKNOWN, AUTHOR_UNKNOWN ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            case BOOK_LOANED_OUT -> ResponseEntity.status(HttpStatus.CONFLICT).body("Book is already loaned out");
        };
    }
}
