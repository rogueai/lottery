package com.test.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long ticketId) {
        super("could not find ticket '" + ticketId + "'.");
    }
}