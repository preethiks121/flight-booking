package com.flightbooking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingResponse {
    private UUID id;
    private long flightId;
    private String flightNumber;
    private String passengerName;
    private String passengerEmail;
    private int seatCount;
    private LocalDateTime bookingTime;
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public long getFlightId() { return flightId; }
    public void setFlightId(long flightId) { this.flightId = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
