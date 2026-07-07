package com.flightbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull(message = "Flight ID is required")
    @Positive(message = "Flight ID must be a positive number")
    private Long flightId;

    @NotBlank(message = "Passenger name is required")
    @Size(min = 2, max = 100, message = "Passenger name must be between 2 and 100 characters")
    private String passengerName;

    @NotBlank(message = "Passenger email is required")
    @Email(message = "Invalid email format")
    private String passengerEmail;

    @NotNull(message = "Seat count is required")
    @Min(value = 1, message = "At least 1 seat must be booked")
    private Integer seatCount;

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }
}
