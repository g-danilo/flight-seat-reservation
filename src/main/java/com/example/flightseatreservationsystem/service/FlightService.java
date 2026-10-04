package com.example.flightseatreservationsystem.service;

import com.example.flightseatreservationsystem.dto.CreateFlightRequest;
import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.exception.ResourceNotFoundException;
import com.example.flightseatreservationsystem.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
            throw new ResourceNotFoundException("Flight not found. Flight id: %d".formatted(id));
        }

        flightRepository.deleteById(id);
    }

    public List<Flight> searchFlights(LocalDate date, String departureAirport, String arrivalAirport) {

        List<Flight> flights = flightRepository.searchFlights(departureAirport, arrivalAirport);

        if (date == null) {
            return flights;
        }

        return flights.stream()
                .filter(flight ->
                        flight.getDepartureTime().toLocalDate().equals(date))
                .toList();
    }
}
