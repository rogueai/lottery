package com.test.rest.service;

import com.test.LotteryApplication;
import com.test.model.Ticket;
import com.test.model.TicketModelFactory;
import com.test.repository.TicketRepository;
import com.test.rest.exception.AmendNotAllowedException;
import com.test.rest.exception.TicketNotFoundException;
import com.test.rest.exception.TicketPersistException;
import com.test.rest.service.impl.TicketServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(LotteryApplication.class)
@Transactional
public class TicketServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @Before
    public void before() {
        ticketRepository.deleteAll();
    }

    @Test
    public void ticketLinesOrder() throws Exception {
        final Ticket ticket = ticketService.createTicket(Optional.of(3));
        // overwrite lines outcome
        IntStream.range(0, ticket.getLines().size()).forEach(i ->
                ticket.getLines().get(i).setOutcome(i)
        );

        ticketService.saveTicket(ticket);
        // we need a fresh copy of the ticket since the ordering happens at select time
        Ticket updated = ticketService.getTicket(ticket.getId());

        assertEquals(3, updated.getLines().size());
        assertEquals(2, updated.getLines().get(0).getOutcome().intValue());
        assertEquals(1, updated.getLines().get(1).getOutcome().intValue());
        assertEquals(0, updated.getLines().get(2).getOutcome().intValue());
    }

    @Test
    public void getTicket_null() throws Exception {
        exception.expect(InvalidDataAccessApiUsageException.class);
        ticketService.getTicket(null);
    }

    @Test
    public void getTicket_notFound() throws Exception {
        exception.expect(TicketNotFoundException.class);
        ticketService.getTicket(0L);
    }

    @Test
    public void amendTicket_empty() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticketService.amendTicket(ticket.getId(), Optional.empty());
        ticketService.getTicket(ticket.getId());
        assertTrue(ticket.getLines().size() == 1);
    }

    @Test
    public void amendTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticketService.amendTicket(ticket.getId(), Optional.of(1));
        ticket = ticketService.getTicket(ticket.getId());
        assertTrue(ticket.getLines().size() == 2);
    }

    /**
     * Verify that an {@link AmendNotAllowedException} is thrown when trying to amend lines to a
     * checked ticket
     */
    @Test
    public void amendTicket_checked() throws Exception {
        exception.expect(AmendNotAllowedException.class);
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticket.setStatus(Ticket.Status.CHECKED);
        ticketService.saveTicket(ticket);
        ticketService.amendTicket(ticket.getId(), Optional.of(1));
    }

    @Test
    public void getTickets_empty() throws Exception {
        List<Ticket> tickets = ticketService.getTickets();
        assertNotNull(tickets);
        assertTrue(tickets.isEmpty());
    }

    @Test
    public void getTickets() throws Exception {
        ticketService.createTicket(Optional.of(1));
        ticketService.createTicket(Optional.of(1));
        ticketService.createTicket(Optional.of(1));
        List<Ticket> tickets = ticketService.getTickets();
        assertNotNull(tickets);
        assertEquals(3, tickets.size());
    }

    @Test
    public void createTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        assertNotNull(ticket);
        assertNotNull(ticket.getId());
    }

    @Test
    public void createTicket_null() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        assertNotNull(ticket);
        assertNotNull(ticket.getId());
    }

    @Test
    public void checkTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticket = ticketService.checkTicket(ticket.getId());
        assertNotNull(ticket);
        assertEquals(Ticket.Status.CHECKED, ticket.getStatus());
    }

    @Test
    public void checkTicket_null() throws Exception {
        exception.expect(InvalidDataAccessApiUsageException.class);
        ticketService.checkTicket(null);
    }

    @Test
    public void saveTicket_null() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ticketService.saveTicket(null);
    }

    @Test
    public void saveTicket_exception() {
        exception.expect(TicketPersistException.class);
        TicketRepository mockRepository = mock(TicketRepository.class);
        doThrow(new IllegalArgumentException()).when(mockRepository).save(any(Ticket.class));
        TicketModelFactory mockFactory = mock(TicketModelFactory.class);
        TicketService mockService = new TicketServiceImpl(mockRepository, mockFactory);
        mockService.saveTicket(new Ticket());
    }


}
