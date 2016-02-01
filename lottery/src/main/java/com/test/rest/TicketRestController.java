package com.test.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.test.model.Line;
import com.test.model.Status;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@RestController
public class TicketRestController {

    private static final Logger log = LoggerFactory.getLogger(TicketRestController.class);

    public static final String DEFAULT_LINES = "3";
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketRestController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @JsonView(View.Basic.class)
    @RequestMapping(value = "/ticket/{ticketId}", method = RequestMethod.GET)
    public Ticket getTicket(@PathVariable Long ticketId) {
        return this.validateAndGet(ticketId);
    }

    @JsonView(View.Basic.class)
    @RequestMapping(value = "/ticket/{ticketId}", method = RequestMethod.PUT)
    public Ticket amendTicket(@PathVariable Long ticketId, @RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Integer lines) {
        this.validateAndGet(ticketId);
        Ticket ticket = this.ticketRepository.findOne(ticketId);
        if (ticket.getStatus() == Status.CHECKED) {
            throw new AmendNotAllowedException(ticket.getId());
        }
        if (lines != null) {
            for (int i = 0; i < lines; i++) {
                createLine(ticket);
            }
        }
        ticketRepository.save(ticket);
        return ticket;
    }

    @RequestMapping(value = "/status/{ticketId}", method = RequestMethod.PUT)
    public Ticket checkTicket(@PathVariable Long ticketId) {
        this.validateAndGet(ticketId);
        Ticket ticket = this.ticketRepository.findOne(ticketId);
        ticket.setStatus(Status.CHECKED);
        ticketRepository.save(ticket);
        return ticket;
    }

//    @JsonView(View.Basic.class)
//    @RequestMapping(value = "/ticket", method = RequestMethod.POST)
//    public Ticket createTicket(@RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Optional<Integer> lines) {
//        Ticket ticket = new Ticket();
//        lines.ifPresent(integer -> {
//            for (int i = 0; i < integer; i++) {
//                createLine(ticket);
//            }
//        });
//        return this.ticketRepository.save(ticket);
//    }

    @RequestMapping(value = "/ticket", method = RequestMethod.POST)
    ResponseEntity<?> createTicket(@RequestParam(value = "lines", defaultValue = DEFAULT_LINES) Optional<Integer> lines) {

        Ticket ticket = new Ticket();
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> createLine(ticket))
        );
        ticketRepository.save(ticket);
        HttpHeaders httpHeaders = new HttpHeaders();

        URI ticketUri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(ticket.getId()).toUri();
        httpHeaders.setLocation(ticketUri);

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
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

    @JsonView(View.Basic.class)
    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    public List<Ticket> getTickets() {
        log.debug("retrieved all tickets");
        return this.ticketRepository.findAll();
    }

    private Ticket validateAndGet(Long ticketId) {
        Optional<Ticket> optional = Optional.ofNullable(this.ticketRepository.findOne(ticketId));
        return optional.orElseThrow(() -> new TicketNotFoundException(ticketId));
    }
}
