package dev.stormgui.slim_garage.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name = "garage")
class GarageEntity(
    @Column(name = "des_name")
    val name: String,

    @OneToMany(mappedBy = "garage")
    val sectors: List<SectorEntity> = mutableListOf()
) : BaseEntity()
