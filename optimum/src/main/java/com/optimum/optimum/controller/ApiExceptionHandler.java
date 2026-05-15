package com.optimum.optimum.controller;

import com.optimum.optimum.dto.ApiDtos.ProblemDetails;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetails> handleBadRequest(IllegalArgumentException exception) {
        ProblemDetails problem = new ProblemDetails(
                "https://api.optimum.video/errors/bad-request",
                "Requete invalide",
                400,
                exception.getMessage(),
                "req-" + UUID.randomUUID(),
                Map.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
