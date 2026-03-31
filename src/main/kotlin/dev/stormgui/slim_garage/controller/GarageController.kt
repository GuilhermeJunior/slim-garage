package dev.stormgui.slim_garage.controller

import dev.stormgui.slim_garage.domain.dtos.GarageResponse
import dev.stormgui.slim_garage.service.GarageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/garage")
class GarageController(
    private val garageService: GarageService
) {
    @GetMapping()
    fun getGarage(): ResponseEntity<GarageResponse> {
        return ResponseEntity.ok(garageService.getGarage())
    }
}
