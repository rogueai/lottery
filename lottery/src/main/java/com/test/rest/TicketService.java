package com.test.rest;

import com.test.model.Line;
import com.test.model.Status;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket amendTicket(Long ticketId, Optional<Integer> lines) {
        Ticket ticket = validateAndGet(ticketId);
        if (ticket.getStatus() == Status.CHECKED) {
            throw new AmendNotAllowedException(ticketId);
        }
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> createLine(ticket))
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
        ticket.setStatus(Status.CHECKED);
        return ticketRepository.save(ticket);
    }

    public Ticket createTicket(Optional<Integer> lines) {
        Ticket ticket = new Ticket();
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> createLine(ticket))
        );
        return ticketRepository.save(ticket);
    }

    private void createLine(Ticket ticket) {
        Line line = new Line();
        line.setValues(new Random().ints(3, 0, 3).toArray());
        line.setOutcome(calculateOutcome(line));
        line.setTicket(ticket);
        ticket.getLines().add(line);
    }

    private int calculateOutcome(Line line) {
        int[] values = line.getValues();
        int sum = Arrays.stream(values).sum();
        if (sum == 2) {
            return 10;
        }
        boolean allMatch = Arrays.stream(values).allMatch(s -> s == values[0]);
        if (allMatch) {
            return 5;
        }
        boolean firstDiffers = IntStream.range(1, values.length).map(i -> values[i]).noneMatch(s -> s == values[0]);
        if (firstDiffers) {
            return 1;
        }
        return 0;
    }

    private Ticket validateAndGet(Long ticketId) {
        Optional<Ticket> optional = Optional.ofNullable(this.ticketRepository.findOne(ticketId));
        return optional.orElseThrow(() -> new TicketNotFoundException(ticketId));
    }
}
