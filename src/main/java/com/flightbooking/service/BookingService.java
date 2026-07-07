package com.flightbooking.service;

import com.flightbooking.dto.gen.BookingRequest;
import com.flightbooking.dto.gen.BookingResponse;
import com.flightbooking.dto.gen.BookingResponse.StatusEnum;
import com.flightbooking.dto.gen.FlightResponse;
import com.flightbooking.exception.BookingNotFoundException;
import com.flightbooking.exception.FlightNotFoundException;
import com.flightbooking.exception.OverbookingException;
import com.flightbooking.model.Booking;
import com.flightbooking.model.BookingStatus;
import com.flightbooking.model.Flight;
import com.flightbooking.repository.BookingRepository;
import com.flightbooking.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final Object flightLock = new Object();

    public BookingService(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    public BookingResponse createBooking(BookingRequest request) {
        synchronized (flightLock) {
            Flight flight = flightRepository.findById(request.getFlightId())
                    .orElseThrow(() -> new FlightNotFoundException(request.getFlightId()));

            if (flight.getBookedSeats() + request.getSeatCount() > flight.getTotalSeats()) {
                throw new OverbookingException(
                        flight.getFlightNumber(),
                        request.getSeatCount(),
                        flight.getAvailableSeats());
            }
            flight.setBookedSeats(flight.getBookedSeats() + request.getSeatCount());

            Booking booking = new Booking(
                    UUID.randomUUID(),
                    flight.getId(),
                    request.getPassengerName(),
                    request.getPassengerEmail(),
                    request.getSeatCount(),
                    LocalDateTime.now(),
                    BookingStatus.CONFIRMED
            );
            bookingRepository.save(booking);

            return toBookingResponse(booking, flight);
        }
    }

    public BookingResponse getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        Flight flight = flightRepository.findById(booking.getFlightId())
                .orElseThrow(() -> new FlightNotFoundException(booking.getFlightId()));
        return toBookingResponse(booking, flight);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> {
                    Flight flight = flightRepository.findById(booking.getFlightId()).orElse(null);
                    return toBookingResponse(booking, flight);
                })
                .toList();
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::toFlightResponse)
                .toList();
    }

    private BookingResponse toBookingResponse(Booking booking, Flight flight) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setFlightId(booking.getFlightId());
        response.setFlightNumber(flight != null ? flight.getFlightNumber() : null);
        response.setPassengerName(booking.getPassengerName());
        response.setPassengerEmail(booking.getPassengerEmail());
        response.setSeatCount(booking.getSeatCount());
        response.setBookingTime(booking.getBookingTime().atOffset(ZoneOffset.UTC));
        response.setStatus(StatusEnum.valueOf(booking.getStatus().name()));
        return response;
    }

    private FlightResponse toFlightResponse(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setOrigin(flight.getOrigin());
        response.setDestination(flight.getDestination());
        response.setDepartureTime(flight.getDepartureTime().atOffset(ZoneOffset.UTC));
        response.setArrivalTime(flight.getArrivalTime().atOffset(ZoneOffset.UTC));
        response.setTotalSeats(flight.getTotalSeats());
        response.setAvailableSeats(flight.getAvailableSeats());
        return response;
    }
}
