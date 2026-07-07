package com.flightbooking.controller;

import com.flightbooking.dto.gen.BookingResponse;
import com.flightbooking.dto.gen.BookingResponse.StatusEnum;
import com.flightbooking.dto.gen.FlightResponse;
import com.flightbooking.model.Flight;
import com.flightbooking.repository.BookingRepository;
import com.flightbooking.repository.FlightRepository;
import com.flightbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        flightRepository.findAll().forEach(f -> {
            f.setBookedSeats(0);
            flightRepository.save(f);
        });
    }

    @Test
    void createBooking_ShouldReturn201_WhenValid() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightId":1,"passengerName":"John Doe","passengerEmail":"john@example.com","seatCount":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passengerName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createBooking_ShouldReturn422_WhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightId":0,"passengerName":"","passengerEmail":"not-an-email","seatCount":0}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void createBooking_ShouldReturn409_WhenOverbooking() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightId":1,"passengerName":"John","passengerEmail":"j@t.com","seatCount":999}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void getBooking_ShouldReturn200_WhenFound() throws Exception {
        String json = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightId":1,"passengerName":"Jane","passengerEmail":"jane@test.com","seatCount":1}
                                """))
                .andReturn().getResponse().getContentAsString();

        String id = json.substring(json.indexOf("\"id\":\"") + 6, json.indexOf("\"", json.indexOf("\"id\":\"") + 6));

        mockMvc.perform(get("/api/v1/bookings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerName").value("Jane"));
    }

    @Test
    void getBooking_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookings_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllFlights_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").value("BA123"))
                .andExpect(jsonPath("$[0].origin").value("London (LHR)"))
                .andExpect(jsonPath("$[0].totalSeats").value(200))
                .andExpect(jsonPath("$[0].availableSeats").value(200));
    }
}
