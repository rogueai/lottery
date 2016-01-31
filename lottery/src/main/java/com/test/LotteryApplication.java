package com.test;

import com.test.repository.TicketRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LotteryApplication {

    @Bean
    CommandLineRunner init(TicketRepository ticketRepository) {
        return strings -> {

        };
    }

    public static void main(String[] args) {
        SpringApplication.run(LotteryApplication.class, args);
    }

}
