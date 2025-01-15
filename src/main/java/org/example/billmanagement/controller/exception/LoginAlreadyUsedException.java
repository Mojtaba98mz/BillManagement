package org.example.billmanagement.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.util.Date;

public class LoginAlreadyUsedException extends ErrorResponseException {

    public LoginAlreadyUsedException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(),null);
    }

    private static ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Login name is already in use");
        problemDetail.setTitle("Login Already Used");
        problemDetail.setType(URI.create(ErrorConstants.LOGIN_ALREADY_USED_TYPE));
        problemDetail.setProperty("errorCode", "LOGIN_ALREADY_USED");
        problemDetail.setProperty("timestamp", new Date());
        return problemDetail;
    }
}
