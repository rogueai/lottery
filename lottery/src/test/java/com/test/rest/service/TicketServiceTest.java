package com.test.rest.service;

import com.test.LotteryApplication;
import com.test.model.Ticket;
import com.test.rest.exception.AmendNotAllowedException;
import com.test.rest.exception.TicketNotFoundException;
import org.junit.Assert;
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
    private TicketService ticketService;

    @Test
    public void ticketLinesOrder() {
        final Ticket ticket = ticketService.createTicket(Optional.of(3));
        // overwrite lines outcome
        IntStream.range(0, ticket.getLines().size()).forEach(i ->
                ticket.getLines().get(i).setOutcome(i)
        );

        ticketService.save(ticket);

        Ticket one = ticketService.getTicket(ticket.getId());

        Assert.assertEquals(3, one.getLines().size());
        Assert.assertEquals(2, one.getLines().get(0).getOutcome().intValue());
        Assert.assertEquals(1, one.getLines().get(1).getOutcome().intValue());
        Assert.assertEquals(0, one.getLines().get(2).getOutcome().intValue());
    }

    @Test
    public void getTicket_null() {
        exception.expect(InvalidDataAccessApiUsageException.class);
        ticketService.getTicket(null);
    }

    @Test
    public void getTicket_notFound() {
        exception.expect(TicketNotFoundException.class);
        ticketService.getTicket(0l);
    }

    @Test
    public void amendTicket_empty() {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticketService.amendTicket(ticket.getId(), Optional.empty());
        ticketService.getTicket(ticket.getId());
        Assert.assertTrue(ticket.getLines().size() == 1);
    }

    @Test
    public void amendTicket() {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticketService.amendTicket(ticket.getId(), Optional.of(1));
        ticket = ticketService.getTicket(ticket.getId());
        Assert.assertTrue(ticket.getLines().size() == 2);
    }

    @Test
    public void amendTicket_checked() {
        exception.expect(AmendNotAllowedException.class);
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        ticket.setStatus(Ticket.Status.CHECKED);
        ticketService.save(ticket);
        ticketService.amendTicket(ticket.getId(), Optional.of(1));
    }

    @Test
    public void getTickets_empty() {
        List<Ticket> tickets = ticketService.getTickets();
        Assert.assertNotNull(tickets);
        Assert.assertTrue(tickets.isEmpty());
    }

    @Test
    public void getTickets() {
        ticketService.createTicket(Optional.of(1));
        ticketService.createTicket(Optional.of(1));
        ticketService.createTicket(Optional.of(1));
        List<Ticket> tickets = ticketService.getTickets();
        Assert.assertNotNull(tickets);
        Assert.assertEquals(3, tickets.size());
    }

}
