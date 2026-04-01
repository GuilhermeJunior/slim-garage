package dev.stormgui.slim_garage.service

import dev.stormgui.slim_garage.domain.dtos.RevenueEntry
import dev.stormgui.slim_garage.domain.dtos.RevenueResponse
import dev.stormgui.slim_garage.exceptions.NotFoundException
import dev.stormgui.slim_garage.repositories.ParkingRegistryEntityRepository
import dev.stormgui.slim_garage.repositories.SectorRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class RevenueService(
    private val parkingRegistryRepository: ParkingRegistryEntityRepository,
    private val sectorRepository: SectorRepository
) {
    fun getRevenue(revenueEntry: RevenueEntry): RevenueResponse {
        val sector = sectorRepository.findByName(revenueEntry.sector)
            ?: throw NotFoundException("Sector=${revenueEntry.sector} not found")
        val startDate = revenueEntry.date.atStartOfDay()
        val endDate = revenueEntry.date.plusDays(1).atStartOfDay()
        val registers = parkingRegistryRepository.findBySectorAndDate(sector, startDate, endDate)

        if (registers.isEmpty()) {
            throw NotFoundException("no registers")
        }

        val amount = registers.map { it.revenue }
            .fold(BigDecimal.ZERO, BigDecimal::add)
        
        return RevenueResponse(
            amount = amount,
            currency = "BRL",
            timestamp = LocalDateTime.now()
        )
    }
}
