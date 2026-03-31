package dev.stormgui.slim_garage.controller

import dev.stormgui.slim_garage.domain.dtos.RevenueEntry
import dev.stormgui.slim_garage.domain.dtos.RevenueResponse
import dev.stormgui.slim_garage.service.RevenueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/revenue")
class RevenueController(
    private val revenueService: RevenueService
) {

    @GetMapping
    fun getRevenue(request: RevenueEntry): ResponseEntity<RevenueResponse> {
        return ResponseEntity.ok(revenueService.getRevenue(request))
    }
}
