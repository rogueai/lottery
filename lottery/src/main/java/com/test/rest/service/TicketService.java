package com.test.rest.service;

import com.test.model.TicketModelFactory;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import com.test.rest.exception.AmendNotAllowedException;
import com.test.rest.exception.TicketNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * S
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Service
@Transactional
public class TicketService {

    private static Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final TicketModelFactory modelFactory;

    @Autowired
    public TicketService(TicketRepository ticketRepository, TicketModelFactory modelFactory) {
        this.ticketRepository = ticketRepository;
        this.modelFactory = modelFactory;
    }

    /**
     * Add additional lines to the ticket. If the ticket's status is {@link com.test.model.Ticket.Status#CHECKED},
     * no lines can be added and a {@link AmendNotAllowedException} is thrown.
     *
     * @param ticketId the ticket id
     * @param lines    the optional number of lines to add
     * @return the updated ticket
     * @throws AmendNotAllowedException if the ticket is already checked
     */
    public Ticket amendTicket(Long ticketId, Optional<Integer> lines) throws AmendNotAllowedException {
        logger.debug("Amending ticket (id: {}) with additional {} lines", ticketId, lines);
        Ticket ticket = validateAndGet(ticketId);
        if (ticket.getStatus() != Ticket.Status.NEW) {
            throw new AmendNotAllowedException(ticketId);
        }
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> {
                    modelFactory.createLine(ticket);
                })
        );
        return ticketRepository.save(ticket);
    }

    /**
     * Retrieve a single ticket
     *
     * @param ticketId the ticket id
     * @return the ticket
     */
    public Ticket getTicket(Long ticketId) {
        logger.debug("Retrieving ticket with id {}", ticketId);
        return validateAndGet(ticketId);
    }

    /**
     * Retrieve a list of all the tickets
     *
     * @return the list of all tickets
     */
    public List<Ticket> getTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        logger.debug("Retrieved {} tickets", tickets.size());
        return tickets;
    }

    /**
     * Check the ticket, effectively setting its status to {@link com.test.model.Ticket.Status#CHECKED}
     *
     * @param ticketId the ticket id
     * @return the checked ticket
     */
    public Ticket checkTicket(Long ticketId) {
        logger.debug("Checking status of ticket with id {}", ticketId);
        Ticket ticket = validateAndGet(ticketId);
        ticket.setStatus(Ticket.Status.CHECKED);
        return ticketRepository.save(ticket);
    }

    /**
     * Create a ticket with a (optional) number of lines
     *
     * @param lines an optional number of lines to initialize the ticket
     * @return the new ticket
     */
    public Ticket createTicket(Optional<Integer> lines) {
        Ticket ticket = modelFactory.createTicket(lines);
        ticket = ticketRepository.save(ticket);
        logger.debug("Created new ticket with id {}", ticket.getId());
        return ticket;
    }

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    private Ticket validateAndGet(Long ticketId) {
        Optional<Ticket> optional = Optional.ofNullable(this.ticketRepository.findOne(ticketId));
        return optional.orElseThrow(() -> new TicketNotFoundException(ticketId));
    }
}
