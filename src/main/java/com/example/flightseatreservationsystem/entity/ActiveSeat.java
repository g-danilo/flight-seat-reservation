package com.example.flightseatreservationsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "active_seats", uniqueConstraints = {
        @UniqueConstraint(name = "uk_active_booking",
                          columnNames = "booking_id"
        ),
        @UniqueConstraint(name = "uk_active_flight_seat",
                         columnNames ={"flight_id", "seat_number"}
        )
})
public class ActiveSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private int seatNumber;

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
