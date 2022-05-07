package com.movies.cinesalsaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
public class FailedDependencyException extends RuntimeException {
    public FailedDependencyException() { super(); }
    public FailedDependencyException(String message, Throwable cause) { super(message, cause); }
    public FailedDependencyException(String message) { super(message); }
    public FailedDependencyException(Throwable cause) { super(cause); }
}
