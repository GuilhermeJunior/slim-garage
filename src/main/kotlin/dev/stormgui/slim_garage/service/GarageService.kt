package dev.stormgui.slim_garage.service

import dev.stormgui.slim_garage.domain.dtos.GarageResponse
import dev.stormgui.slim_garage.domain.dtos.Sector
import dev.stormgui.slim_garage.domain.dtos.Spot
import dev.stormgui.slim_garage.repositories.SectorRepository
import dev.stormgui.slim_garage.repositories.SpotEntityRepository
import org.springframework.stereotype.Service

@Service
class GarageService(
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotEntityRepository
) {

    fun getGarage(): GarageResponse {
        val sectors = sectorRepository.findAll()
        val sectorsList = mutableListOf<Sector>()

        for (sector in sectors) {
            val spots = spotRepository.findBySector(sector)
            val spotsList = mutableListOf<Spot>()

            for (spot in spots) {
                spotsList.add(
                    Spot(
                        id = spot.id,
                        sector = sector.name,
                        lat = spot.latitude,
                        lng = spot.longitude
                    )
                )
            }
            sectorsList.add(
                Sector(
                    sector = sector.name,
                    basePrice = sector.basePrice,
                    maxCapacity = sector.maxCapacity,
                    spots = spotsList
                )
            )
        }
        return GarageResponse(sectorsList)
    }
}