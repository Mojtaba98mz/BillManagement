package org.example.billmanagement.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginAlreadyUsedException.class)
    public ResponseEntity<ProblemDetail> handleLoginAlreadyUsedException(LoginAlreadyUsedException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleWrongUsernamePassword(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Bad credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleArgumentValidationException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid argument");
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("errors","argument_validation_exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

}