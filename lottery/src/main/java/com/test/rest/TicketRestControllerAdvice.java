package com.test.rest;

import com.test.rest.exception.AmendNotAllowedException;
import com.test.rest.exception.TicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller Advice that handles exception thrown at a service layer, and returns the appropriate response body and HTTP status.
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@ControllerAdvice
public class TicketRestControllerAdvice {

    @ExceptionHandler(TicketNotFoundException.class)
    public void ticketNotFoundExceptionHandler(HttpServletResponse response, TicketNotFoundException e) throws IOException {
        sendError(HttpStatus.NOT_FOUND, response, e);
    }

    @ExceptionHandler(AmendNotAllowedException.class)
    public void amendNotAllowedExceptionExceptionHandler(HttpServletResponse response, AmendNotAllowedException e) throws IOException {
        sendError(HttpStatus.METHOD_NOT_ALLOWED, response, e);
    }

    private void sendError(HttpStatus status, HttpServletResponse response, Throwable e) throws IOException {
        response.sendError(status.value(), e.getMessage());
    }

}
