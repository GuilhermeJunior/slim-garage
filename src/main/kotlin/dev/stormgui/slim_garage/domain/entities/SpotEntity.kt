package dev.stormgui.slim_garage.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "spot")
class SpotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null,
    var sector: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    @ManyToOne
    @JoinColumn(name = "garageId")
    var garage: GarageEntity
)
