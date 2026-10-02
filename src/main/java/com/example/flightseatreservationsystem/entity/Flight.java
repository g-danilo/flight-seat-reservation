package com.example.flightseatreservationsystem.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name ="flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flightNumber;

    @Column(nullable = false)
    private String departureAirport;

    @Column(nullable = false)
    private ZonedDateTime departureTime;

    @Column(nullable = false)
    private String arrivalAirport;

    @Column(nullable = false)
    private ZonedDateTime arrivalTime;

    @Column(nullable = false)
    private int totalSeats;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public void setDepartureTime(ZonedDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public void setArrivalTime(ZonedDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}
