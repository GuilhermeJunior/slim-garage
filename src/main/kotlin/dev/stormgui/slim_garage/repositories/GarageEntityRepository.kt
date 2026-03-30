 package dev.stormgui.slim_garage.repositories

import dev.stormgui.slim_garage.domain.entities.GarageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GarageEntityRepository : JpaRepository<GarageEntity, Long> {
}