package com.test.rest;

/**
 * Tag interfaces to annotate fields that belong to a particular {@link com.fasterxml.jackson.annotation.JsonView}.
 * <p/>
 * A view allows to filter object serialization when different paths require a different representation
 *
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public class View {

    /**
     * The basic view will exclude the list of {@link com.test.model.Line}s from a serialized
     * {@link com.test.model.Ticket}
     */
    public interface Basic {
    }

}
