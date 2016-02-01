package com.test.rest.service;

import com.test.model.TicketModelfactory;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import com.test.rest.exception.AmendNotAllowedException;
import com.test.rest.exception.TicketNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketModelfactory modelFactory;

    @Autowired
    public TicketService(TicketRepository ticketRepository, TicketModelfactory modelFactory) {
        this.ticketRepository = ticketRepository;
        this.modelFactory = modelFactory;
    }

    public Ticket amendTicket(Long ticketId, Optional<Integer> lines) {
        Ticket ticket = validateAndGet(ticketId);
        if (ticket.getStatus() == Ticket.Status.CHECKED) {
            throw new AmendNotAllowedException(ticketId);
        }
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> modelFactory.createLine(ticket))
        );
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long ticketId) {
        return validateAndGet(ticketId);
    }

    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    public Ticket checkTicket(Long ticketId) {
        Ticket ticket = validateAndGet(ticketId);
        ticket.setStatus(Ticket.Status.CHECKED);
        return ticketRepository.save(ticket);
    }

    public Ticket createTicket(Optional<Integer> lines) {
        Ticket ticket = modelFactory.createTicket(lines);
        return ticketRepository.save(ticket);
    }

    private Ticket validateAndGet(Long ticketId) {
        Optional<Ticket> optional = Optional.ofNullable(this.ticketRepository.findOne(ticketId));
        return optional.orElseThrow(() -> new TicketNotFoundException(ticketId));
    }
}
