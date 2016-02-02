package com.test.rest;

import com.test.rest.exception.AmendNotAllowedException;
import com.test.rest.exception.TicketNotFoundException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller Advice that handles exceptions thrown at a service layer, and returns the appropriate response body and HTTP status.
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@ControllerAdvice
class TicketRestControllerAdvice {

    /**
     * Thrown by the service layer when trying to retrieve a ticket and passing an empty or wrong id.
     */
    @ExceptionHandler(TicketNotFoundException.class)
    public void ticketNotFoundExceptionHandler(HttpServletResponse response, TicketNotFoundException e) throws IOException {
        sendError(HttpStatus.NOT_FOUND, response, e);
    }

    @ExceptionHandler(AmendNotAllowedException.class)
    public void amendNotAllowedExceptionExceptionHandler(HttpServletResponse response, AmendNotAllowedException e) throws IOException {
        sendError(HttpStatus.METHOD_NOT_ALLOWED, response, e);
    }

    /**
     * Thrown when trying to access the repository in an invalid way. This is for example when trying to find
     * an entity passing a null id.
     * Normally, this would get caught at the service layer, by the id being optional, so this is most likely
     * due to implementation error.
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public void invalidDataAccessApiUsageExceptionHandler(HttpServletResponse response, InvalidDataAccessApiUsageException e) throws IOException {
        sendError(HttpStatus.INTERNAL_SERVER_ERROR, response, e);
    }

    /**
     * Thrown when a invalid argument is provided to the service, e.g.: trying to save a null Ticket due to
     * an implementation error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentExceptionHandler(HttpServletResponse response, IllegalArgumentException e) throws IOException {
        sendError(HttpStatus.INTERNAL_SERVER_ERROR, response, e);
    }

    private void sendError(HttpStatus status, HttpServletResponse response, Throwable e) throws IOException {
        response.sendError(status.value(), e.getMessage());
    }

}
