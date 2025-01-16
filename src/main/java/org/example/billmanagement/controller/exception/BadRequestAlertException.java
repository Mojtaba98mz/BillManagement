package org.example.billmanagement.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class BadRequestAlertException extends ErrorResponseException {

    public BadRequestAlertException(String message, String entityName, String errorKey) {
        super(HttpStatus.BAD_REQUEST, createProblemDetail(message, entityName, errorKey), null);
    }

    private static ProblemDetail createProblemDetail(String message, String entityName, String errorKey) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setProperty("entityName", entityName);
        problemDetail.setProperty("errorKey", errorKey);
        return problemDetail;
    }
}
