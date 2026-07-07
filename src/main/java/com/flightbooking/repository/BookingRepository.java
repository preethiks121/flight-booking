package com.flightbooking.repository;

import com.flightbooking.model.Booking;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BookingRepository {
    private final ConcurrentHashMap<UUID, Booking> bookings = new ConcurrentHashMap<>();

    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findById(UUID id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public Collection<Booking> findAll() {
        return bookings.values();
    }
}
