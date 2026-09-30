package com.example.flightseatreservationsystem.service;

import com.example.flightseatreservationsystem.dto.CreateFlightRequest;
import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.repository.FlightRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Flight createFlight(CreateFlightRequest request) {
        Flight flight = new Flight();

        flight.setFlightNumber(request.flightNumber());
        flight.setDepartureAirport(request.departureAirport());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalAirport(request.arrivalAirport());
        flight.setArrivalTime(request.arrivalTime());
        flight.setTotalSeats(request.totalSeats());

        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Flight not found");
        }

        flightRepository.deleteById(id);
    }
}
