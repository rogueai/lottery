package com.test.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.test.model.Ticket;
import com.test.rest.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@RestController
public class TicketRestController {

    private static Logger logger = LoggerFactory.getLogger(TicketRestController.class);

    public static final String DEFAULT_LINES = "3";
    private static final Logger log = LoggerFactory.getLogger(TicketRestController.class);
    private final TicketService ticketService;

    @Autowired
    public TicketRestController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @JsonView(View.Basic.class)
    @RequestMapping(value = "/ticket/{ticketId}", method = RequestMethod.GET)
    public Ticket getTicket(@PathVariable Long ticketId) {
        return ticketService.getTicket(ticketId);
    }

    @JsonView(View.Basic.class)
    @RequestMapping(value = "/ticket/{ticketId}", method = RequestMethod.PUT)
    public Ticket amendTicket(@PathVariable Long ticketId, @RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Optional<Integer> lines) {
        Ticket ticket = ticketService.amendTicket(ticketId, lines);
        return ticket;
    }

    @RequestMapping(value = "/status/{ticketId}", method = RequestMethod.PUT)
    public Ticket checkTicket(@PathVariable Long ticketId) {
        return ticketService.checkTicket(ticketId);
    }

    @RequestMapping(value = "/ticket", method = RequestMethod.POST)
    public ResponseEntity<?> createTicket(@RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Optional<Integer> lines) {
        Ticket ticket = ticketService.createTicket(lines);

        HttpHeaders httpHeaders = new HttpHeaders();

        URI ticketUri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(ticket.getId()).toUri();
        httpHeaders.setLocation(ticketUri);
        logger.debug("Creating new ticket with location: {}", ticketUri);
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @JsonView(View.Basic.class)
    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    public List<Ticket> getTickets() {
        return ticketService.getTickets();
    }

}
