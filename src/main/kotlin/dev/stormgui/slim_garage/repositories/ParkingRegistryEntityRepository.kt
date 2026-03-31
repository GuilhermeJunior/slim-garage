package dev.stormgui.slim_garage.repositories

import dev.stormgui.slim_garage.domain.entities.ParkingRegistryEntity
import dev.stormgui.slim_garage.domain.entities.SectorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime


interface ParkingRegistryEntityRepository : JpaRepository<ParkingRegistryEntity, Long> {

    fun findByLicensePlate(licensePlate: String): ParkingRegistryEntity?

    @Query(
        ("SELECT p FROM ParkingRegistryEntity p " +
                "JOIN p.spot s " +
                "JOIN s.sector ss " +
                "WHERE ss = :sector " +
                "AND p.createdAt >= :startOfDay " +
                "AND p.createdAt < :endOfDay")
    )
    fun findBySectorAndDate(
        sector: SectorEntity,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): List<ParkingRegistryEntity>

}