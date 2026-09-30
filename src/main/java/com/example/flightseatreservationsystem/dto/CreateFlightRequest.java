package com.example.flightseatreservationsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record CreateFlightRequest(
        @NotBlank String flightNumber,
        @NotBlank String departureAirport,
        ZonedDateTime departureTime,
        @NotBlank String arrivalAirport,
        ZonedDateTime arrivalTime,
        @Positive int totalSeats
) {
}
