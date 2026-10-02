package com.example.flightseatreservationsystem.repository;

import com.example.flightseatreservationsystem.entity.ActiveSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActiveSeatRepository extends JpaRepository<ActiveSeat, Long> {

    @Modifying
    @Query("""
            DELETE FROM ActiveSeat a
            WHERE a.booking.bookingStatus = BookingStatus.EXPIRED
    """)
    int deleteExpiredSeats();

    @Modifying
    @Query("""
            DELETE FROM ActiveSeat a
            WHERE a.booking.id = :bookingId
    """)
    int deleteByBookingId(@Param("bookingId") Long bookingId);

    List<ActiveSeat> findByFlightId(Long flightId);
}
