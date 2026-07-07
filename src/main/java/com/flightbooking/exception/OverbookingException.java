package com.flightbooking.exception;

public class OverbookingException extends RuntimeException {
    public OverbookingException(String flightNumber, int requested, int available) {
        super("Not enough seats available on flight " + flightNumber
                + ". Requested: " + requested + ", Available: " + available);
    }
}
