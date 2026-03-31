package dev.stormgui.slim_garage.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "parking_registry")
class ParkingRegistryEntity(
    @ManyToOne
    @JoinColumn(name = "spot_id")
    val spot: SpotEntity,

    @Column(name = "dat_start")
    val datStart: LocalDateTime,

    @Column(name = "dat_end")
    var datEnd: LocalDateTime? = null,

    @Column(name = "des_license_plate")
    val licensePlate: String,

    @Column(name = "qty_revenue")
    var revenue: BigDecimal,

    @Column(name = "pct_price_discount")
    val percentPriceDiscount: BigDecimal,

    @Column(name = "pct_price_increase")
    val percentPriceIncrease: BigDecimal
) : BaseEntity()
