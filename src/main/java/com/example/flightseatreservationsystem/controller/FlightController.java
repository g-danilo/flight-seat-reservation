package com.example.flightseatreservationsystem.controller;

import com.example.flightseatreservationsystem.dto.CreateBookingRequest;
import com.example.flightseatreservationsystem.entity.Booking;
import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.service.BookingService;
import com.example.flightseatreservationsystem.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Flight> searchFlights(@RequestParam(required = false, name = "date") LocalDate date,
                                      @RequestParam(required = false, name = "departureAirport") String departureAirport,
                                      @RequestParam(required = false, name = "arrivalAirport") String arrivalAirport) {
        return flightService.searchFlights(date, departureAirport, arrivalAirport);
    }

    @PostMapping("/{id}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@PathVariable Long id, @Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(id, request);
    }

    @GetMapping("/{id}/seats")
    public List<Integer> getSeatAvailability(@PathVariable Long id) {
        return bookingService.getSeatAvailability(id);
    }

}
