package dev.stormgui.slim_garage.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "sector")
class SectorEntity(
    @Column(name = "des_name")
    val name: String,

    @ManyToOne
    @JoinColumn(name = "garage_id")
    val garage: GarageEntity,

    @Column(name = "base_price")
    val basePrice: BigDecimal,

    @Column(name = "max_capacity")
    val maxCapacity: Int
) : BaseEntity()
