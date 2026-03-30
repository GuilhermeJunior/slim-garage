package dev.stormgui.slim_garage.repositories

import dev.stormgui.slim_garage.domain.entities.ParkingRegistryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ParkingRegistryEntityRepository : JpaRepository<ParkingRegistryEntity, Long> {

    fun findByLicensePlate(licensePlate: String): ParkingRegistryEntity?

}