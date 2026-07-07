package com.flightbooking.exception;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(long flightId) {
        super("Flight not found with id: " + flightId);
    }
}
