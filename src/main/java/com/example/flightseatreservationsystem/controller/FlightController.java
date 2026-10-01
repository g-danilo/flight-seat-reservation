package com.example.flightseatreservationsystem.controller;

import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.service.FlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public List<Flight> searchFlights(@RequestParam(required = false, name = "date") LocalDate date,
                                      @RequestParam(required = false, name = "departureAirport") String departureAirport,
                                      @RequestParam(required = false, name = "arrivalAirport") String arrivalAirport) {
        return flightService.searchFlights(date, departureAirport, arrivalAirport);
    }
}
