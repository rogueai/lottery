package com.test.model;

import com.test.LotteryApplication;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(LotteryApplication.class)
public class TicketModelFactoryTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private TicketModelFactory factory;

    @Test
    public void calculateOutcome_nullLine() throws Exception {
        exception.expect(IllegalArgumentException.class);
        factory.calculateOutcome(null);
    }

    @Test
    public void calculateOutcome_nullValues() throws Exception {
        exception.expect(IllegalArgumentException.class);
        factory.calculateOutcome(new Line());
    }

    /**
     * Expected outcome (in bold)
     * <ul>
     * <li>10: sum == 2</li>
     * <li>5: all the same</li>
     * <li>1: 2nd && 3rd != 1st</li>
     * <li><b>0: other</b></li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void calculateOutcome_1() throws Exception {
        Line line = new Line();
        line.setValues(new int[]{1, 2, 1});
        int outcome = factory.calculateOutcome(line);
        assertEquals(0, outcome);
    }

    /**
     * Expected outcome (in bold)
     * <ul>
     * <li>10: sum == 2</li>
     * <li>5: all the same</li>
     * <li><b>1: 2nd && 3rd != 1st</b></li>
     * <li>0: other</b></li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void calculateOutcome_2() throws Exception {
        Line line = new Line();
        line.setValues(new int[]{1, 2, 3});
        int outcome = factory.calculateOutcome(line);
        assertEquals(1, outcome);
    }

    /**
     * Expected outcome (in bold)
     * <ul>
     * <li>10: sum == 2</li>
     * <li>5: all the same</li>
     * <li><b>1: 2nd && 3rd != 1st</b></li>
     * <li>0: other</b></li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void calculateOutcome_3() throws Exception {
        Line line = new Line();
        line.setValues(new int[]{1, 2, 2});
        int outcome = factory.calculateOutcome(line);
        assertEquals(1, outcome);
    }

    /**
     * Expected outcome (in bold)
     * <ul>
     * <li>10: sum == 2</li>
     * <li><b>5: all the same</b></li>
     * <li>1: 2nd && 3rd != 1st</li>
     * <li>0: other</b></li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void calculateOutcome_4() throws Exception {
        Line line = new Line();
        line.setValues(new int[]{1, 1, 1});
        int outcome = factory.calculateOutcome(line);
        assertEquals(5, outcome);
    }

    /**
     * Expected outcome (in bold)
     * <ul>
     * <li><b>10: sum == 2</b></li>
     * <li>5: all the same</li>
     * <li>1: 2nd && 3rd != 1st</li>
     * <li>0: other</b></li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void calculateOutcome_5() throws Exception {
        Line line = new Line();
        line.setValues(new int[]{1, 1, 0});
        int outcome = factory.calculateOutcome(line);
        assertEquals(10, outcome);
    }

    /**
     * Expected outcome (in bold)
     * <ul>
     * <li><b>10: sum == 2</b></li>
     * <li>5: all the same</li>
     * <li>1: 2nd && 3rd != 1st</li>
     * <li>0: other</b></li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void calculateOutcome_6() throws Exception {
        Line line = new Line();
        line.setValues(new int[]{0, 0, 2});
        int outcome = factory.calculateOutcome(line);
        assertEquals(10, outcome);
    }

    @Test
    public void createTicket_null() throws Exception {
        Ticket ticket = factory.createTicket(null);
        assertNotNull(ticket);
        assertEquals(0, ticket.getLines().size());
    }

    @Test
    public void createTicket_empty() throws Exception {
        Ticket ticket = factory.createTicket(Optional.empty());
        assertNotNull(ticket);
        assertEquals(0, ticket.getLines().size());
    }

    @Test
    public void createTicket() throws Exception {
        Ticket ticket = factory.createTicket(Optional.of(10));
        assertNotNull(ticket);
        assertEquals(10, ticket.getLines().size());
    }

    @Test
    public void createLine_null() throws Exception {
        Line line = factory.createLine(null);
        assertNotNull(line);
        assertNull(line.getTicket());
    }

    @Test
    public void createLine() throws Exception {
        Line line = factory.createLine(new Ticket());
        assertNotNull(line);
        assertNotNull(line.getTicket());
        assertEquals(1, line.getTicket().getLines().size());
    }

    @Test
    public void createLine_ticketHasNullLines() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setLines(null);
        Line line = factory.createLine(ticket);
        assertNotNull(line);
        assertNotNull(line.getTicket());
        assertEquals(1, line.getTicket().getLines().size());
    }
}
