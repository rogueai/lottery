package com.test.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.test.rest.View;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
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
