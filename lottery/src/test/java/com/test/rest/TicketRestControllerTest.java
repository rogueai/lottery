package com.test.rest;

import com.test.LotteryApplication;
import com.test.model.Line;
import com.test.model.TicketModelfactory;
import com.test.model.Ticket;
import com.test.repository.TicketRepository;
import com.test.rest.service.TicketService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LotteryApplication.class)
@Transactional
@WebAppConfiguration
public class TicketRestControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter httpMessageConverter;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.httpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter
        ).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null", httpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * When requesting an unknown resource an HttpStatus 404 is returned
     *
     * @throws Exception
     */
    @Test
    public void genericNotFound() throws Exception {
        mockMvc.perform(get("/foo"))
                .andExpect(status().isNotFound());
    }

    /**
     * When a Ticket is not found an HttpStatus 404 is returned
     *
     * @throws Exception
     */
    @Test
    public void ticketNotFound() throws Exception {
        mockMvc.perform(get("/ticket/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTicket() throws Exception {
        Ticket ticket = ticketService.createTicket((Optional.of(1)));
        mockMvc.perform(get("/ticket/" + ticket.getId()).contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.linesCount", is(1)));
    }

    /**
     * Lines are not serialized for GET /ticket/{ticketId}
     *
     * @throws Exception
     */
    @Test
    public void getTicket_linesNotSerialized() throws Exception {
        Ticket ticket = ticketService.createTicket((Optional.empty()));
        mockMvc.perform(get("/ticket/" + ticket.getId()).contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.lines").doesNotExist());
    }

    /**
     * Lines are not serialized for GET /ticket
     *
     * @throws Exception
     */
    @Test
    public void getTickets_linesNotSerialized() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.empty());
        mockMvc.perform(get("/ticket").contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lines").doesNotExist());
    }

    /**
     * Amend ticket successful for default lines count
     * <p/>
     * e.g.: PUT /ticket/{ticketId}
     *
     * @throws Exception
     */
    @Test
    public void amendTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        mockMvc.perform(put("/ticket/" + ticket.getId()).contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.linesCount", is(4)));
    }

    /**
     * Amend ticket successful with provided lines parameter
     * <p/>
     * e.g.: PUT /ticket/{ticketId}?lines={linesCount}
     *
     * @throws Exception
     */
    @Test
    public void amendTicket_linesParam() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        mockMvc.perform(put("/ticket/" + ticket.getId() + "?lines=1").contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.linesCount", is(2)));
    }

    /**
     * Get ticket returns 400: BAD_REQUEST when provided with wrong ticket id
     * <p/>
     * e.g.: PUT /ticket/foo
     *
     * @throws Exception
     */
    @Test
    public void getTicket_wrongId() throws Exception {
        mockMvc.perform(get("/ticket/foo").contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    /**
     * Amend ticket returns 400: BAD_REQUEST when provided with wrong parameter
     * <p/>
     * e.g.: PUT /ticket/{ticketId}?lines=abc
     *
     * @throws Exception
     */
    @Test
    public void amendTicket_wrongParamValue() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        mockMvc.perform(put("/ticket/" + ticket.getId() + "?lines=abc").contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    /**
     * Amend ticket successful and parameter is ignored when unknown
     * <p/>
     * e.g.: PUT /ticket/{ticketId}?foo=bar
     *
     * @throws Exception
     */
    @Test
    public void amendTicket_unknownParam() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        mockMvc.perform(put("/ticket/" + ticket.getId() + "?foo=bar").contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.linesCount", is(4)));
    }

    /**
     * Displaying the status of a ticket, its status is changed to CHECKED and lines are serialized
     * <p/>
     * e.g.: PUT /status/{ticketId}
     *
     * @throws Exception
     */
    @Test
    public void checkTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        mockMvc.perform(put("/status/" + ticket.getId()).contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status", is("CHECKED")))
                .andExpect(jsonPath("$.linesCount", is(1)))
                .andExpect(jsonPath("$.lines").exists())
                .andExpect(jsonPath("$.lines", hasSize(1)));
    }

    /**
     * Amending a ticket is not allowed if the ticket's status is CHECKED
     *
     * @throws Exception
     */
    @Test
    public void checkTicket_amendNotAllowed() throws Exception {
        Ticket ticket = ticketService.createTicket(Optional.of(1));
        mockMvc.perform(put("/status/" + ticket.getId()).contentType(contentType))
                .andExpect(status().isOk());
        mockMvc.perform(put("/ticket/" + ticket.getId()).contentType(contentType))
                .andExpect(status().isMethodNotAllowed());
    }


    @Test
    public void createTicket() throws Exception {
        String ticketJson = toJson(new Ticket());
        ResultActions actions = this.mockMvc.perform(post("/ticket")
                .contentType(contentType)
                .content(ticketJson));
        actions.andExpect(status().isCreated());
        Ticket ticket = ticketService.getTickets().get(0);
        actions.andExpect(header().string("location", endsWith("/ticket/" + ticket.getId())));
    }

    /**
     * Create ticket returns 400: BAD_REQUEST when provided with wrong parameter
     * <p/>
     * e.g.: POST /ticket?lines=abc
     *
     * @throws Exception
     */
    @Test
    public void createTicket_wrongParamValue() throws Exception {
        String ticketJson = toJson(new Ticket());
        this.mockMvc.perform(post("/ticket?lines=abc")
                .contentType(contentType)
                .content(ticketJson))
                .andExpect(status().isBadRequest());

    }

    /**
     * Create ticket successful and parameter is ignored when unknown
     * <p/>
     * e.g.: POST /ticket?foo=bar
     *
     * @throws Exception
     */
    @Test
    public void createTicket_unknownParam() throws Exception {
        String ticketJson = toJson(new Ticket());
        this.mockMvc.perform(post("/ticket?foo=bar")
                .contentType(contentType)
                .content(ticketJson))
                .andExpect(status().isCreated());
    }

    /**
     * Lines are not serialized for GET /ticket
     *
     * @throws Exception
     */
    @Test
    public void getTickets() throws Exception {
        ticketService.createTicket(Optional.of(1));
        ticketService.createTicket(Optional.of(1));

        mockMvc.perform(get("/ticket").contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    protected String toJson(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        httpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
