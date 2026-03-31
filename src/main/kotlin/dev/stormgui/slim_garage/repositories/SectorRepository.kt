package dev.stormgui.slim_garage.repositories

import dev.stormgui.slim_garage.domain.entities.SectorEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SectorRepository : JpaRepository<SectorEntity, Long> {

    fun findByName(name: String): SectorEntity?
}
