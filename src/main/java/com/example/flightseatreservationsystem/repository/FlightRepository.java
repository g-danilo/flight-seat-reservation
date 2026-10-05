package com.example.flightseatreservationsystem.repository;

import com.example.flightseatreservationsystem.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
            SELECT f
            FROM Flight f
            WHERE (:departureAirport IS NULL OR LOWER(f.departureAirport) = LOWER(:departureAirport))
            AND (:arrivalAirport IS NULL OR LOWER(f.arrivalAirport) = LOWER(:arrivalAirport))
            """)
    List<Flight> searchFlights(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport
    );

    boolean existsByFlightNumber(String flightNumber);
}
