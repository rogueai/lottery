package com.test.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
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

    private static final int LINE_SIZE = 3;
    private static final int LINE_VALUE_MIN = 0;
    private static final int LINE_VALUE_MAX = 2;

    private static Logger logger = LoggerFactory.getLogger(TicketModelFactory.class);

    public Ticket createTicket(Optional<Integer> lines) {
        Ticket ticket = new Ticket();
        if (lines != null) {
            lines.ifPresent(value ->
                    IntStream.range(0, value).forEach(i -> createLine(ticket))
            );
        }
        logger.debug("Created ticket with {} lines.", ticket.getLines().size());
        return ticket;
    }

    /**
     * Creates a {@link Line} and set the correct parent-child relationships.
     *
     * @param ticket the parent ticket
     */
    public Line createLine(Ticket ticket) {

        Line line = new Line();
        line.setValues(new Random().ints(LINE_SIZE, LINE_VALUE_MIN, LINE_VALUE_MAX + 1).toArray());
        int outcome = calculateOutcome(line);
        logger.debug("Created line with random values: {} - calculated outcome: {}", line.getValues(), outcome);
        line.setOutcome(outcome);
        if (ticket != null) {
            if (ticket.getLines() == null){
                ticket.setLines(new ArrayList<>());
            }
            line.setTicket(ticket);
            ticket.getLines().add(line);
        } else {
            logger.error("Cannot create association for line: null ticket");
        }
        return line;
    }

    int calculateOutcome(Line line) {
        Assert.notNull(line);
        Assert.notNull(line.getValues());
        int[] values = line.getValues();
        // create a stream and sum all its elements
        int sum = Arrays.stream(values).sum();
        if (sum == 2) {
            return 10;
        }
        // all items match the first one
        boolean allMatch = Arrays.stream(values).allMatch(s -> s == values[0]);
        if (allMatch) {
            return 5;
        }
        // iterate starting from the second item, and check that none match the first
        boolean firstDiffers = IntStream.range(1, values.length).map(i -> values[i]).noneMatch(s -> s == values[0]);
        if (firstDiffers) {
            return 1;
        }
        return 0;
    }

}
