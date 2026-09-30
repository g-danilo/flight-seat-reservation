package com.example.flightseatreservationsystem.repository;

import com.example.flightseatreservationsystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
