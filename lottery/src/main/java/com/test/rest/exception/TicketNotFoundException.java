package com.test.rest.exception;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long ticketId) {
        super("could not find ticket '" + ticketId + "'.");
    }
}