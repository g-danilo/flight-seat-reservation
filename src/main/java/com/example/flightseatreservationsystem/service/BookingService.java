package com.example.flightseatreservationsystem.service;

import com.example.flightseatreservationsystem.dto.CreateBookingRequest;
import com.example.flightseatreservationsystem.entity.ActiveSeat;
import com.example.flightseatreservationsystem.entity.Booking;
import com.example.flightseatreservationsystem.entity.BookingStatus;
import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.exception.SeatAlreadyBookedException;
import com.example.flightseatreservationsystem.repository.ActiveSeatRepository;
import com.example.flightseatreservationsystem.repository.BookingRepository;
import com.example.flightseatreservationsystem.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookingService {

    private static final long HOLD_DURATION_MINUTES = 2;
    private static final long BOOKING_CUTOFF_MINUTES = 45;

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final ActiveSeatRepository activeSeatRepository;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, ActiveSeatRepository activeSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.activeSeatRepository = activeSeatRepository;
    }

    @Transactional
    public Booking createBooking(Long flightId, CreateBookingRequest request) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() ->
                new RuntimeException("Flight not found"));

        ZonedDateTime now = ZonedDateTime.now(flight.getDepartureTime().getZone());
        ZonedDateTime cutoff = flight.getDepartureTime().minusMinutes(BOOKING_CUTOFF_MINUTES);

        if(!now.isBefore(cutoff)) {
            throw new RuntimeException("Booking window has closed");
        }

        if(request.seatNumber() > flight.getTotalSeats()) {
            throw new RuntimeException("Invalid seat number");
        }

        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setPassengerName(request.passengerName());
        booking.setSeatNumber(request.seatNumber());
        booking.setBookingStatus(BookingStatus.HELD);
        booking.setCreatedAt(now);
        booking.setHoldExpiresAt(now.plusMinutes(HOLD_DURATION_MINUTES));
        booking = bookingRepository.saveAndFlush(booking);

        ActiveSeat activeSeat = new ActiveSeat();
        activeSeat.setBooking(booking);
        activeSeat.setFlight(flight);
        activeSeat.setSeatNumber(request.seatNumber());
        try {
            activeSeatRepository.saveAndFlush(activeSeat);
        } catch (DataIntegrityViolationException e) {
            throw new SeatAlreadyBookedException();
        }
        return booking;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void expireBooking() {
        bookingRepository.expireBookings(ZonedDateTime.now());
        activeSeatRepository.deleteExpiredSeats();
    }

    public List<Integer> getSeatAvailability(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() ->
                new RuntimeException("Flight not found"));

        Set<Integer> unavailableSeats = activeSeatRepository
                .findByFlightId(flightId)
                .stream()
                .map(ActiveSeat::getSeatNumber)
                .collect(Collectors.toSet());

        return IntStream.rangeClosed(1, flight.getTotalSeats())
                .filter(seatNumber -> !unavailableSeats.contains(seatNumber))
                .boxed()
                .toList();

    }

    //TODO reject expired
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                                new RuntimeException("Booking not found"));
        booking.setBookingStatus(BookingStatus.CANCELLED);

        activeSeatRepository.deleteByBookingId(bookingId);
    }

    //TODO add conirmf
}
