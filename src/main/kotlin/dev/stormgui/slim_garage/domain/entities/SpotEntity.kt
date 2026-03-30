package dev.stormgui.slim_garage.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "spot")
class SpotEntity(
    @ManyToOne
    @JoinColumn(name = "sector_id")
    val sector: SectorEntity,

    @Column(name = "nr_latitude")
    val latitude: Long,

    @Column(name = "nr_longitude")
    val longitude: Long,

    @Column(name = "ie_taken")
    var isTaken: Boolean
) : BaseEntity()
