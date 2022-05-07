package com.movies.cinesalsaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException() { super(); }
    public ServiceUnavailableException(String message, Throwable cause) { super(message, cause); }
    public ServiceUnavailableException(String message) { super(message); }
    public ServiceUnavailableException(Throwable cause) { super(cause); }
}
