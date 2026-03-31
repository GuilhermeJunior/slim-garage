package dev.stormgui.slim_garage.domain.dtos

import java.math.BigDecimal

data class GarageResponse(
    val garage: List<Sector>
)

data class Sector(
    val sector: String,
    val basePrice: BigDecimal,
    val maxCapacity: Int,
    val spots: List<Spot>
)

data class Spot(
    val id: Long,
    val sector: String,
    val lat: Long,
    val lng: Long
)
