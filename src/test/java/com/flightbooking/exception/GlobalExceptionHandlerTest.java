package com.flightbooking.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleFlightNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Booking Not Found"))
                .andExpect(jsonPath("$.type").value("error/booking-not-found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }

    @Test
    void handleOverbooking_ShouldReturn409() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightId\":1,\"passengerName\":\"JJ\",\"passengerEmail\":\"j@t.com\",\"seatCount\":999}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Overbooking"))
                .andExpect(jsonPath("$.type").value("error/overbooking"))
                .andExpect(jsonPath("$.detail").value(
                        "Not enough seats available on flight BA123. Requested: 999, Available: 200"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }

    @Test
    void handleValidationFailure_ShouldReturn422() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.type").value("error/validation"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }

    @Test
    void handleMethodNotAllowed_ShouldReturn405() throws Exception {
        mockMvc.perform(put("/api/v1/bookings"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.title").value("Method Not Allowed"))
                .andExpect(jsonPath("$.type").value("error/method-not-allowed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }

    @Test
    void handleBadRequest_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not valid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }

    @Test
    void handleGeneralException_ShouldReturn500() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", "not-a-uuid"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.type").value("error/internal-server-error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }
}
