package com.test.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Factory responsible of creating domain model objects.
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Component
public class TicketModelFactory {

    private static Logger logger = LoggerFactory.getLogger(TicketModelFactory.class);

    public Ticket createTicket(Optional<Integer> lines) {
        Ticket ticket = new Ticket();
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> createLine(ticket))
        );
        return ticket;
    }

    /**
     * Creates a {@link Line} and set the correct parent-child relationships.
     *
     * @param ticket
     */
    public void createLine(Ticket ticket) {

        Line line = new Line();
        line.setValues(new Random().ints(3, 0, 3).toArray());
        int outcome = calculateOutcome(line);
        logger.debug("Created line with random values: {} - calculated outcome:", line.getValues(), outcome);
        line.setOutcome(outcome);
        line.setTicket(ticket);
        ticket.getLines().add(line);
    }

    protected int calculateOutcome(Line line) {
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

}
