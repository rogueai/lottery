package com.test.rest.service;

import com.test.model.Ticket;
import com.test.rest.exception.AmendNotAllowedException;

import java.util.List;
import java.util.Optional;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public interface TicketService {

    /**
     * Add additional lines to the ticket. If the ticket's status is {@link com.test.model.Ticket.Status#CHECKED},
     * no lines can be added and a {@link AmendNotAllowedException} is thrown.
     *
     * @param ticketId the ticket id
     * @param lines    the optional number of lines to add
     * @return the updated ticket
     * @throws AmendNotAllowedException if the ticket is already checked
     */
    Ticket amendTicket(Long ticketId, Optional<Integer> lines) throws AmendNotAllowedException;

    /**
     * Retrieve a single ticket
     *
     * @param ticketId the ticket id
     * @return the ticket
     */
    Ticket getTicket(Long ticketId);

    /**
     * Retrieve a list of all the tickets
     *
     * @return the list of all tickets
     */
    List<Ticket> getTickets();

    /**
     * Check the ticket, effectively setting its status to {@link Ticket.Status#CHECKED}
     *
     * @param ticketId the ticket id
     * @return the checked ticket
     */
    Ticket checkTicket(Long ticketId);

    /**
     * Create a ticket with a (optional) number of lines
     *
     * @param lines an optional number of lines to initialize the ticket
     * @return the new ticket
     */
    Ticket createTicket(Optional<Integer> lines);

    /**
     * Persist the passed {@link Ticket}
     * @param ticket the ticket to be saved
     * @return the saved ticket
     */
    Ticket saveTicket(Ticket ticket);
}
