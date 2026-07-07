package com.flightbooking.config;

import com.flightbooking.model.Flight;
import com.flightbooking.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataPreloader implements CommandLineRunner {

    private final FlightRepository flightRepository;

    public DataPreloader(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        flightRepository.save(new Flight(0, "BA123", "London (LHR)", "New York (JFK)",
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(18).withMinute(30), 200));

        flightRepository.save(new Flight(0, "AF456", "Paris (CDG)", "Tokyo (NRT)",
                LocalDateTime.now().plusDays(2).withHour(22).withMinute(15),
                LocalDateTime.now().plusDays(3).withHour(16).withMinute(45), 150));

        flightRepository.save(new Flight(0, "LH789", "Frankfurt (FRA)", "Dubai (DXB)",
                LocalDateTime.now().plusDays(3).withHour(14).withMinute(30),
                LocalDateTime.now().plusDays(3).withHour(22).withMinute(10), 180));

        flightRepository.save(new Flight(0, "EK321", "Dubai (DXB)", "Singapore (SIN)",
                LocalDateTime.now().plusDays(4).withHour(3).withMinute(45),
                LocalDateTime.now().plusDays(4).withHour(15).withMinute(20), 120));
    }
}
