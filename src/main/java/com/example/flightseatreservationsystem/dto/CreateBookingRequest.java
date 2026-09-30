package com.example.flightseatreservationsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateBookingRequest(
    @NotBlank String passengerName,
    @Positive int seatNumber
) {
}
