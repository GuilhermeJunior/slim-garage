package dev.stormgui.slim_garage.service

import dev.stormgui.slim_garage.domain.dtos.WebHookRequest
import dev.stormgui.slim_garage.domain.dtos.WebhookResponse
import dev.stormgui.slim_garage.domain.entities.ParkingRegistryEntity
import dev.stormgui.slim_garage.domain.entities.SpotEntity
import dev.stormgui.slim_garage.domain.enums.EventType
import dev.stormgui.slim_garage.repositories.ParkingRegistryEntityRepository
import dev.stormgui.slim_garage.repositories.SpotEntityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

@Service
class ParkingRegistryService(
    private val parkingRegistryRepository: ParkingRegistryEntityRepository,
    private val spotRepository: SpotEntityRepository
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ParkingRegistryService::class.java)
        const val MINIMUM_FREE_TIME = 30L
    }

    @Transactional
    fun register(entry: WebHookRequest): WebhookResponse {
        LOG.info("M=register payload=$entry")

        return when (entry.eventType) {
            EventType.ENTRY -> park(entry)
            EventType.EXIT -> exit(entry)
            else -> error("Unknown event type: ${entry.eventType}")
        }
    }

    fun park(entry: WebHookRequest): WebhookResponse {
        val spots = spotRepository.findByIsTaken(false)

        require(spots.isNotEmpty()) { "Garage has no spots" }

        val spot = spots.first()
        spot.isTaken = true

        val (discount, increase) = calculatePercentage(spot, spots.count())

        val register = ParkingRegistryEntity(
            spot = spot,
            datStart = entry.entryTime,
            licensePlate = entry.licensePlate,
            revenue = BigDecimal.ZERO,
            percentPriceDiscount = discount,
            percentPriceIncrease = increase
        )

        spotRepository.save(spot)
        parkingRegistryRepository.save(register)

        return WebhookResponse(
            licensePlate = register.licensePlate,
            entryTime = register.datStart,
            eventType = EventType.PARKED,
            lat = spot.latitude,
            lng = spot.longitude
        )
    }

    fun exit(entry: WebHookRequest): WebhookResponse {
        val register = parkingRegistryRepository.findByLicensePlate(entry.licensePlate)
            ?: throw RuntimeException("License Plate not found")

        val spot = register.spot
        spot.isTaken = false

        spotRepository.save(spot)

        register.datEnd = LocalDateTime.now()
        register.revenue = calculateRevenue(register, spot)

        parkingRegistryRepository.save(register)

        return WebhookResponse(
            licensePlate = register.licensePlate,
            entryTime = register.datStart,
            eventType = EventType.EXIT,
            lat = spot.latitude,
            lng = spot.longitude
        )
    }

    private fun calculateRevenue(
        register: ParkingRegistryEntity,
        spot: SpotEntity
    ): BigDecimal {
        val elapsedTime = Duration.between(register.datStart, register.datEnd)

        val elapsedMinutes = elapsedTime.toMinutes()

        if (elapsedMinutes <= MINIMUM_FREE_TIME) {
            LOG.info("M=calculateRevenue free parking within first 30 minutes, elapsed minutes=$elapsedMinutes")
            return BigDecimal.ZERO
        }

        val hoursCharged = ceil(elapsedMinutes / 60.0).toLong()

        val basePrice = spot.sector.basePrice

        val price = basePrice.minus(basePrice.multiply(register.percentPriceDiscount))
            .add(basePrice.multiply(register.percentPriceIncrease))

        return price.multiply(hoursCharged.toBigDecimal())
    }

    private fun calculatePercentage(spot: SpotEntity, allSpotsCount: Int): Pair<BigDecimal, BigDecimal> {
        val spotsSectorCount = spotRepository.countBySectorAndIsTaken(spot.sector, true)

        val sectorOccupationPercent = (spotsSectorCount.toBigDecimal() * allSpotsCount.toBigDecimal()) * BigDecimal(100)

        var discount = BigDecimal.ZERO
        var increase = BigDecimal.ZERO

        when {
            sectorOccupationPercent < BigDecimal(25) -> discount = BigDecimal("0.10")
            sectorOccupationPercent <= BigDecimal(50) -> discount = BigDecimal.ZERO
            sectorOccupationPercent <= BigDecimal(75) -> increase = BigDecimal("0.10")
            else -> increase = BigDecimal("0.25")
        }

        return Pair(discount, increase)
    }
}
