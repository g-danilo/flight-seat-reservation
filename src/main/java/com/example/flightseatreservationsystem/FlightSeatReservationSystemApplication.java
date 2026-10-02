package com.example.flightseatreservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlightSeatReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightSeatReservationSystemApplication.class, args);
    }

}
