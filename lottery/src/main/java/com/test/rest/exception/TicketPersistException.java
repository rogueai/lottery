package com.test.rest.exception;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public class TicketPersistException extends RuntimeException {

    public TicketPersistException(Exception e) {
        super("could not save ticket: " + e.getMessage(), e);
    }
}