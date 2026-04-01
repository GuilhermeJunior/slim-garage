package dev.stormgui.slim_garage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import dev.stormgui.slim_garage.domain.dtos.WebHookRequest
import dev.stormgui.slim_garage.domain.dtos.WebhookResponse
import dev.stormgui.slim_garage.domain.enums.EventType
import dev.stormgui.slim_garage.exceptions.FullGarageException
import dev.stormgui.slim_garage.service.ParkingRegistryService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(WebhookController::class)
class WebhookControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {

    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @MockkBean
    private lateinit var service: ParkingRegistryService

    @Test
    fun `should register parking entry successfully`() {
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            entryTime = LocalDateTime.now(),
            eventType = EventType.ENTRY
        )
        val expectedResponse = WebhookResponse(
            licensePlate = "ABC-1234",
            entryTime = request.entryTime,
            eventType = EventType.PARKED,
            lat = 123L,
            lng = 456L
        )
        every { service.register(any()) } returns expectedResponse

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.licensePlate").value(expectedResponse.licensePlate))
            .andExpect(jsonPath("$.eventType").value(expectedResponse.eventType.toString()))
    }

    @Test
    fun `should register parking exit successfully`() {
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            exitTime = LocalDateTime.now(),
            eventType = EventType.EXIT
        )
        val expectedResponse = WebhookResponse(
            licensePlate = "ABC-1234",
            exitTime = request.exitTime,
            eventType = EventType.EXIT
        )
        every { service.register(any()) } returns expectedResponse

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.licensePlate").value(expectedResponse.licensePlate))
            .andExpect(jsonPath("$.eventType").value(expectedResponse.eventType.toString()))
    }

    @Test
    fun `should return status 4xx when IllegalArgumentException`() {
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.ENTRY
        )
        every { service.register(any()) } throws IllegalArgumentException("Invalid event type")

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `should return status 422 status when FullGarageException`() {
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.ENTRY,
            entryTime = LocalDateTime.now()
        )
        every { service.register(any()) } throws FullGarageException("Garage is full, no available spots")

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnprocessableContent)
    }
}
