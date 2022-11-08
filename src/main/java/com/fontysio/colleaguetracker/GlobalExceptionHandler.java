package com.fontysio.colleaguetracker;

import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.status.NoStatusFoundException;
import com.fontysio.colleaguetracker.status.StatusExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = UserNotRegisteredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody StatusResponse handleUserNotRegisteredException() {
        return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "User has not been registered yet! Please first register user with a valid GoogleIDToken");
    }

    @ExceptionHandler(value = GoogleIDTokenInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody StatusResponse handleGoogleIdTokenInvalidException() {
        return new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "The given Google ID token was invalid");
    }

    @ExceptionHandler(value = NoStatusFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody StatusResponse handleNoStatusFoundException() {
        return new StatusResponse(HttpStatus.NOT_FOUND.value(), "The given user has no status");
    }

    @ExceptionHandler(value = StatusExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody StatusResponse handleStatusExpiredException() {
        return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "The status has been expired");
    }
}
