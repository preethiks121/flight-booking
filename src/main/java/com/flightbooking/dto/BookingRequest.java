package com.flightbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class BookingRequest {

    @Positive(message = "Flight ID must be a positive number")
    private long flightId;

    @NotBlank(message = "Passenger name is required")
    private String passengerName;

    @NotBlank(message = "Passenger email is required")
    @Email(message = "Invalid email format")
    private String passengerEmail;

    @Min(value = 1, message = "At least 1 seat must be booked")
    private int seatCount;

    public long getFlightId() { return flightId; }
    public void setFlightId(long flightId) { this.flightId = flightId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }
}
