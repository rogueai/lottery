package com.test.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.test.model.Line;
import com.test.model.Status;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@RestController
public class TicketRestController {

    public static final String DEFAULT_LINES = "3";
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketRestController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @JsonView(View.New.class)
    @RequestMapping(value = "/ticket/{ticketId}", method = RequestMethod.GET)
    public Ticket getTicket(@PathVariable Long ticketId) {
        this.validateTicket(ticketId);
        return this.ticketRepository.findOne(ticketId);
    }

    @JsonView(View.New.class)
    @RequestMapping(value = "/ticket/{ticketId}", method = RequestMethod.PUT)
    public Ticket amendTicket(@PathVariable Long ticketId, @RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Optional<Integer> lines) {
        this.validateTicket(ticketId);
        Ticket ticket = this.ticketRepository.findOne(ticketId);
        if (ticket.getStatus() == Status.CHECKED) {
            throw new AmendNotAllowedException(ticket.getId());
        }
        lines.ifPresent(integer -> {
            for (int i = 0; i < integer; i++) {
                createLine(ticket);
            }
        });
        ticketRepository.save(ticket);
        return ticket;
    }

    @RequestMapping(value = "/status/{ticketId}", method = RequestMethod.PUT)
    public Ticket checkTicket(@PathVariable Long ticketId) {
        this.validateTicket(ticketId);
        Ticket ticket = this.ticketRepository.findOne(ticketId);
        ticket.setStatus(Status.CHECKED);
        ticketRepository.save(ticket);
        return ticket;
    }

    @JsonView(View.New.class)
    @RequestMapping(value = "/ticket", method = RequestMethod.POST)
    public Ticket createTicket(@RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Optional<Integer> lines) {
        Ticket ticket = new Ticket();
        lines.ifPresent(integer -> {
            for (int i = 0; i < integer; i++) {
                createLine(ticket);
            }
        });
        return this.ticketRepository.save(ticket);
    }

    private void createLine(Ticket ticket) {
        Line line = new Line();
        Random r = new Random();
        int seed = 2 - 0 + 1;
        line.setFirst(r.nextInt(seed));
        line.setSecond(r.nextInt(seed));
        line.setThird(r.nextInt(seed));
        line.setTicket(ticket);
        ticket.getLines().add(line);
    }

    @JsonView(View.New.class)
    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    public List<Ticket> getTickets() {
        return this.ticketRepository.findAll();
    }

    private void validateTicket(Long ticketId) {
        Optional<Ticket> optional = Optional.ofNullable(this.ticketRepository.findOne(ticketId));
        optional.orElseThrow(() -> new TicketNotFoundException(ticketId));
    }
}
