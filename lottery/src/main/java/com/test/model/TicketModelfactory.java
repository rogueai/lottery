package com.test.model;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Component
public class TicketModelfactory {

    public Ticket createTicket(Optional<Integer> lines) {
        Ticket ticket = new Ticket();
        lines.ifPresent(value ->
                IntStream.range(0, value).forEach(i -> createLine(ticket))
        );
        return ticket;
    }

    public Line createLine(Ticket ticket) {

        Line line = new Line();
        line.setValues(new Random().ints(3, 0, 3).toArray());
        line.setOutcome(calculateOutcome(line));
        line.setTicket(ticket);
        ticket.getLines().add(line);
        return line;
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
