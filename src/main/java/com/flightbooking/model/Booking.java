package com.flightbooking.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {
    private UUID id;
    private long flightId;
    private String passengerName;
    private String passengerEmail;
    private int seatCount;
    private LocalDateTime bookingTime;
    private BookingStatus status;

    public Booking() {}

    public Booking(UUID id, long flightId, String passengerName, String passengerEmail,
                   int seatCount, LocalDateTime bookingTime, BookingStatus status) {
        this.id = id;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.seatCount = seatCount;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public long getFlightId() { return flightId; }
    public void setFlightId(long flightId) { this.flightId = flightId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
