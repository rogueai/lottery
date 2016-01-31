package com.test.repository;

import com.test.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Massimo Zugno <d3k41n@gmail.com>
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
