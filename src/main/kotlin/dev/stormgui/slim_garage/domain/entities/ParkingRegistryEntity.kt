package dev.stormgui.slim_garage.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "parking_registry")
class ParkingRegistryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "spot_id")
    var spot: SpotEntity,
    var datStart: LocalDateTime,
    var datEnd: LocalDateTime,
    var licensePlate: String,
    var revenue: BigDecimal
) {
}