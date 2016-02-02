package com.test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A {@link Line} contains three randomly-generated values. Each value can have a value of <em>0</em>, <em>1</em> or <em>2</em>.
 * <p/>
 * Based on the three values, an outcome for the line is calculated following these rules:
 * <ul>
 * <li>if the sum of the values is 2, the result is 10</li>
 * <li>if the values are all the same, the result is 5</li>
 * <li>if the second and the third values are different from the first, the result is 1</li>
 * <li>in all the other cases, the result is 0</li>
 * </ul>
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Entity
public class Line {

    @Id
    @GeneratedValue
    private Long id;

    private int[] values;

    private Integer outcome;

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

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }
}
