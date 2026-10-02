package com.example.flightseatreservationsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatAlreadyBookedException extends RuntimeException{

    public SeatAlreadyBookedException() {
        super("The selected seat is no longer available.");
    }
}
