package com.JobApplication.JobApplication.Exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>> handlemethodValidationException(MethodArgumentNotValidException ex)
    {
        Map<String , String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField() , error.getDefaultMessage()
                        )
                );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Map<String , String>> handleResourceNotFoundException(ResourceNotFound ex)
    {
        Map<String , String> error = new HashMap<>();

        error.put("message" , ex.getMessage());

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyException(EmailAlreadyExistException ex)
    {
        Map<String, String> error = new HashMap<>();

        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


}
