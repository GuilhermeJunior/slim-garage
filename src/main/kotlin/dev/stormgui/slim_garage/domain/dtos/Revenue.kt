package dev.stormgui.slim_garage.domain.dtos

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class RevenueEntry(
    val date: LocalDate,
    val sector: String
)

data class RevenueResponse(
    val amount: BigDecimal,
    val currency: String,
    val timestamp: LocalDateTime
)
