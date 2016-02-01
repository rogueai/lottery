package com.test.rest.service;

import com.test.LotteryApplication;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @Test
    public void ticketLinesOrder() {
        final Ticket ticket = ticketService.createTicket(Optional.of(3));
        // overwrite lines outcome
        IntStream.range(0, ticket.getLines().size()).forEach(i ->
                ticket.getLines().get(i).setOutcome(i)
        );
        ticketRepository.save(ticket);
        Ticket one = ticketRepository.findOne(ticket.getId());
        Assert.assertEquals(3, one.getLines().size());
        Assert.assertEquals(2, one.getLines().get(0));
        Assert.assertEquals(1, one.getLines().get(1));
        Assert.assertEquals(0, one.getLines().get(2));
    }

    @Test
    public void getTicket_null() {
        exception.expect(InvalidDataAccessApiUsageException.class);
        ticketService.getTicket(null);
    }

}
