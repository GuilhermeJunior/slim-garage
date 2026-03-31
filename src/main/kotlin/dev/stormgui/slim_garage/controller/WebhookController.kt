package dev.stormgui.slim_garage.controller

import dev.stormgui.slim_garage.domain.dtos.WebHookRequest
import dev.stormgui.slim_garage.domain.dtos.WebhookResponse
import dev.stormgui.slim_garage.service.ParkingRegistryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController(
    private val parkingRegistryService: ParkingRegistryService
) {

    @PostMapping
    fun registerParking(@RequestBody request: WebHookRequest): ResponseEntity<WebhookResponse> {
        return ResponseEntity.ok(parkingRegistryService.register(request))
    }
}