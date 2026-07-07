package com.flightbooking.service;

import com.flightbooking.dto.gen.BookingRequest;
import com.flightbooking.dto.gen.BookingResponse;
import com.flightbooking.dto.gen.FlightResponse;
import com.flightbooking.exception.BookingNotFoundException;
import com.flightbooking.exception.FlightNotFoundException;
import com.flightbooking.exception.OverbookingException;
import com.flightbooking.model.Booking;
import com.flightbooking.model.BookingStatus;
import com.flightbooking.model.Flight;
import com.flightbooking.repository.BookingRepository;
import com.flightbooking.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingServiceTest {

    private FlightRepository flightRepository;
    private BookingRepository bookingRepository;
    private BookingService bookingService;

    private Flight testFlight;
    private static final long FLIGHT_ID = 1L;
    private static final String FLIGHT_NUMBER = "BA123";

    @BeforeEach
    void setUp() {
        flightRepository = new FlightRepository();
        bookingRepository = new BookingRepository();
        bookingService = new BookingService(flightRepository, bookingRepository);

        testFlight = new Flight(FLIGHT_ID, FLIGHT_NUMBER, "LHR", "JFK",
                LocalDateTime.of(2026, 7, 10, 8, 0),
                LocalDateTime.of(2026, 7, 10, 11, 30),
                200);
        flightRepository.save(testFlight);
    }

    private BookingRequest request(long flightId, String name, String email, int seats) {
        BookingRequest req = new BookingRequest();
        req.setFlightId(flightId);
        req.setPassengerName(name);
        req.setPassengerEmail(email);
        req.setSeatCount(seats);
        return req;
    }

    @Test
    void createBooking_ShouldSucceed_WhenSeatsAvailable() {
        BookingResponse response = bookingService.createBooking(request(FLIGHT_ID, "John Doe", "john@example.com", 2));

        assertThat(response).isNotNull();
        assertThat(response.getFlightId()).isEqualTo(FLIGHT_ID);
        assertThat(response.getFlightNumber()).isEqualTo(FLIGHT_NUMBER);
        assertThat(response.getPassengerName()).isEqualTo("John Doe");
        assertThat(response.getPassengerEmail()).isEqualTo("john@example.com");
        assertThat(response.getSeatCount()).isEqualTo(2);
        assertThat(response.getStatus()).isEqualTo(BookingResponse.StatusEnum.CONFIRMED);
        assertThat(response.getId()).isNotNull();
        assertThat(response.getBookingTime()).isNotNull();
    }

    @Test
    void createBooking_ShouldThrow_WhenFlightNotFound() {
        assertThatThrownBy(() -> bookingService.createBooking(request(999L, "John", "j@t.com", 1)))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createBooking_ShouldThrow_WhenOverbooking() {
        testFlight.setBookedSeats(199);
        flightRepository.save(testFlight);

        assertThatThrownBy(() -> bookingService.createBooking(request(FLIGHT_ID, "John", "j@t.com", 2)))
                .isInstanceOf(OverbookingException.class)
                .hasMessageContaining(FLIGHT_NUMBER)
                .hasMessageContaining("2")
                .hasMessageContaining("1");
    }

    @Test
    void createBooking_ShouldIncrementBookedSeats() {
        assertThat(testFlight.getBookedSeats()).isZero();

        bookingService.createBooking(request(FLIGHT_ID, "John", "j@t.com", 2));

        Flight updated = flightRepository.findById(FLIGHT_ID).orElseThrow();
        assertThat(updated.getBookedSeats()).isEqualTo(2);
    }

    @Test
    void createBooking_ShouldSerializeConcurrentRequests() throws InterruptedException {
        testFlight.setTotalSeats(20);
        flightRepository.save(testFlight);

        int threadCount = 3;
        int seatsPerRequest = 10;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger errors = new AtomicInteger(0);

        List<Thread> threads = Stream.generate(() -> new Thread(() -> {
            try {
                latch.await();
                bookingService.createBooking(request(FLIGHT_ID, "User", "u@t.com", seatsPerRequest));
            } catch (OverbookingException e) {
                errors.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        })).limit(threadCount).toList();

        threads.forEach(Thread::start);
        latch.countDown();
        for (Thread t : threads) {
            t.join();
        }

        int expectedSuccessful = 20 / seatsPerRequest;
        assertThat(errors.get()).isEqualTo(threadCount - expectedSuccessful);
        Flight updated = flightRepository.findById(FLIGHT_ID).orElseThrow();
        assertThat(updated.getBookedSeats()).isEqualTo(20);
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenFound() {
        BookingResponse created = bookingService.createBooking(request(FLIGHT_ID, "Jane", "jane@test.com", 1));

        BookingResponse response = bookingService.getBooking(created.getId());

        assertThat(response.getId()).isEqualTo(created.getId());
        assertThat(response.getFlightNumber()).isEqualTo(FLIGHT_NUMBER);
        assertThat(response.getPassengerName()).isEqualTo("Jane");
    }

    @Test
    void getBooking_ShouldThrow_WhenBookingNotFound() {
        assertThatThrownBy(() -> bookingService.getBooking(UUID.randomUUID()))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() {
        bookingService.createBooking(request(FLIGHT_ID, "A", "a@t.com", 1));
        bookingService.createBooking(request(FLIGHT_ID, "B", "b@t.com", 2));

        List<BookingResponse> responses = bookingService.getAllBookings();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(BookingResponse::getPassengerName)
                .containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void getAllFlights_ShouldReturnAllFlights() {
        List<FlightResponse> responses = bookingService.getAllFlights();

        assertThat(responses).hasSize(1);
        FlightResponse response = responses.getFirst();
        assertThat(response.getFlightNumber()).isEqualTo(FLIGHT_NUMBER);
        assertThat(response.getOrigin()).isEqualTo("LHR");
        assertThat(response.getDestination()).isEqualTo("JFK");
        assertThat(response.getTotalSeats()).isEqualTo(200);
        assertThat(response.getAvailableSeats()).isEqualTo(200);
    }
}
