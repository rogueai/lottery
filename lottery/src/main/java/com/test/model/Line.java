package com.test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.test.rest.View;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Entity
public class Line {

    @Id
    @GeneratedValue
    private Long id;

    private int[] values = new int[3];

    @JsonIgnore
    @ManyToOne
    private Ticket ticket;

    public Line() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public int getOutcome() {
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
