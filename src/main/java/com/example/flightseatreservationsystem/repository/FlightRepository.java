package com.example.flightseatreservationsystem.repository;

import com.example.flightseatreservationsystem.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
