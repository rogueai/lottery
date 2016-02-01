package com.test.rest.exception;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public class AmendNotAllowedException extends RuntimeException {

    public AmendNotAllowedException(Long ticketId) {
        super("cannot amend ticket '" + ticketId + "'.");
    }
}