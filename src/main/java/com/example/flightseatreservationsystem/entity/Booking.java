package com.example.flightseatreservationsystem.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private String passengerName;

    @Column(nullable = false)
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime holdExpiresAt;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setHoldExpiresAt(ZonedDateTime holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getHoldExpiresAt() {
        return holdExpiresAt;
    }
}
