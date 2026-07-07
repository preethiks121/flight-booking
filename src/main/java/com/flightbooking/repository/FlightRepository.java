package com.flightbooking.repository;

import com.flightbooking.model.Flight;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FlightRepository {
    private final ConcurrentHashMap<Long, Flight> flights = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Flight save(Flight flight) {
        if (flight.getId() == 0) {
            flight.setId(idGenerator.getAndIncrement());
        }
        flights.put(flight.getId(), flight);
        return flight;
    }

    public Optional<Flight> findById(long id) {
        return Optional.ofNullable(flights.get(id));
    }

    public Collection<Flight> findAll() {
        return flights.values();
    }
}
