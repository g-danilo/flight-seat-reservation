package com.example.flightseatreservationsystem.service;

import com.example.flightseatreservationsystem.dto.CreateBookingRequest;
import com.example.flightseatreservationsystem.entity.ActiveSeat;
import com.example.flightseatreservationsystem.entity.Booking;
import com.example.flightseatreservationsystem.entity.BookingStatus;
import com.example.flightseatreservationsystem.entity.Flight;
import com.example.flightseatreservationsystem.exception.ConflictException;
import com.example.flightseatreservationsystem.exception.ResourceNotFoundException;
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
                new ResourceNotFoundException("Flight not found. Flight id: %d".formatted(flightId)));

        ZonedDateTime now = ZonedDateTime.now(flight.getDepartureTime().getZone());
        ZonedDateTime cutoff = flight.getDepartureTime().minusMinutes(BOOKING_CUTOFF_MINUTES);

        if(!now.isBefore(cutoff)) {
            throw new ConflictException("Booking window has closed");
        }

        if(request.seatNumber() > flight.getTotalSeats()) {
            throw new ConflictException("Invalid seat number");
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
            throw new ConflictException("Seat already booked");
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
                new ResourceNotFoundException("Flight not found. Flight id: %d".formatted(flightId)));

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

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found. Booking id: %d".formatted(bookingId)));

        if (booking.getBookingStatus() == BookingStatus.HELD || booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            activeSeatRepository.deleteByBookingId(bookingId);
        } else {
            throw new ConflictException("This booking cannot be canceled");
        }
    }

    @Transactional
    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found. Booking id: %d".formatted(bookingId)));

        if (booking.getBookingStatus() != BookingStatus.HELD) {
            throw new ConflictException("Only held bookings can be confirmed");
        }

        ZonedDateTime now = ZonedDateTime.now(booking.getHoldExpiresAt().getZone());
        if (!now.isBefore(booking.getHoldExpiresAt())) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            activeSeatRepository.deleteByBookingId(bookingId);
            bookingRepository.save(booking);
            throw new ConflictException("Booking expired");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }
}
