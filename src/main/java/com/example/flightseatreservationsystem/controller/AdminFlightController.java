package com.example.flightseatreservationsystem.controller;

import com.example.flightseatreservationsystem.dto.CreateFlightRequest;
import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/flights")
public class AdminFlightController {

    private final FlightService flightService;

    public AdminFlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Flight createFlight(@Valid @RequestBody CreateFlightRequest request) {
        return flightService.createFlight(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }
}
