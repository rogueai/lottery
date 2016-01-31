package com.test.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class AmendNotAllowedException extends RuntimeException {

    public AmendNotAllowedException(Long ticketId) {
        super("cannot amend ticket '" + ticketId + "'.");
    }
}