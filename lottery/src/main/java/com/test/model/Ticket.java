package com.test.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.test.rest.View;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Ticket} represented by a {@link Status} and a collection of {@link Line}s with outcomes. A ticket is created
 * with a number of lines, that can be <em>amended</em> with additional ones.
 * Whenever a ticket is requested for its status, the ticket becomes {@link Status#CHECKED} and cannot be amended anymore.
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@Entity
public class Ticket {

    @Id
    @GeneratedValue
    @JsonView(View.Basic.class)
    private Long id;

    @JsonView(View.Basic.class)
    @Enumerated()
    private Status status = Status.NEW;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("outcome desc")
    private List<Line> lines = new ArrayList<>();

    public Ticket() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(View.Basic.class)
    public Integer getLinesCount() {
        return lines.size();
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {

        NEW,
        CHECKED

    }
}
