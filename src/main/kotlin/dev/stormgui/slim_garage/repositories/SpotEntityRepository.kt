package dev.stormgui.slim_garage.repositories

import dev.stormgui.slim_garage.domain.entities.SectorEntity
import dev.stormgui.slim_garage.domain.entities.SpotEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpotEntityRepository : JpaRepository<SpotEntity, Long> {
    fun findByIsTaken(isTaken: Boolean): List<SpotEntity>

    fun countByIsTaken(isTaken: Boolean): Long

    fun findBySector(sector: SectorEntity): List<SpotEntity>
}
