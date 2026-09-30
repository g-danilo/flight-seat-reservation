package com.example.flightseatreservationsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record CreateFlightRequest(
        @NotBlank String flightNumber,
        @NotBlank String departureAirport,
        @NotNull ZonedDateTime departureTime,
        @NotBlank String arrivalAirport,
        @NotNull ZonedDateTime arrivalTime,
        @Positive int totalSeats
) {
}
