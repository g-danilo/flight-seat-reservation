package com.example.flightseatreservationsystem.repository;

import com.example.flightseatreservationsystem.entity.Booking;
import com.example.flightseatreservationsystem.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("""
            UPDATE Booking b
            SET b.bookingStatus = BookingStatus.EXPIRED
            WHERE b.bookingStatus = BookingStatus.HELD
            AND b.holdExpiresAt <= :now
    """)
    int expireBookings(@Param("now")ZonedDateTime now);

    List<Booking> findByFlightIdAndBookingStatusIn(Long flightId, List<BookingStatus> bookingStatuses);

    boolean existsByFlightId(Long flightId);
}
