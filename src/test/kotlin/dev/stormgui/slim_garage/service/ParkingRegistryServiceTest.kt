package dev.stormgui.slim_garage.service

import dev.stormgui.slim_garage.domain.dtos.WebHookRequest
import dev.stormgui.slim_garage.domain.entities.GarageEntity
import dev.stormgui.slim_garage.domain.entities.ParkingRegistryEntity
import dev.stormgui.slim_garage.domain.entities.SectorEntity
import dev.stormgui.slim_garage.domain.entities.SpotEntity
import dev.stormgui.slim_garage.domain.enums.EventType
import dev.stormgui.slim_garage.exceptions.FullGarageException
import dev.stormgui.slim_garage.exceptions.NotFoundException
import dev.stormgui.slim_garage.repositories.ParkingRegistryEntityRepository
import dev.stormgui.slim_garage.repositories.SpotEntityRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ParkingRegistryServiceTest {

    @MockK
    private lateinit var parkingRegistryRepository: ParkingRegistryEntityRepository

    @MockK
    private lateinit var spotRepository: SpotEntityRepository

    @InjectMockKs
    private lateinit var parkingRegistryService: ParkingRegistryService

    @BeforeEach
    fun setUp() {
        every { spotRepository.findAll().count() } returns 100
    }

    @Test
    fun `should successfully park a car`() {
        val registrySlot = slot<ParkingRegistryEntity>()
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.ENTRY,
            entryTime = LocalDateTime.now()
        )

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(request.licensePlate) } returns null
        every { spotRepository.findByIsTaken(false) } returns mockSpotList()
        every { spotRepository.countByIsTaken(true) } returns 0

        every { parkingRegistryRepository.save(capture(registrySlot)) } returns mockk()
        every { spotRepository.save(any()) } returns mockk()

        val result = parkingRegistryService.register(request)

        assertEquals(request.licensePlate, registrySlot.captured.licensePlate)
        assertEquals(request.entryTime, registrySlot.captured.datStart)
        assertEquals(EventType.PARKED, result.eventType)
        assertEquals(BigDecimal.ZERO, registrySlot.captured.revenue)
        assertEquals(BigDecimal("0.10"), registrySlot.captured.percentPriceDiscount)
        assertEquals(BigDecimal.ZERO, registrySlot.captured.percentPriceIncrease)
    }

    @ParameterizedTest
    @CsvSource(value = ["20,0.10,0", "50,0,0", "75,0,0.10", "80,0,0.25"])
    fun `should successfully park a car and provide discount and increase price`(spotsTakenCount: Long,
                                                                                expectedDiscount: String,
                                                                                expectedIncrease: String) {
        val registrySlot = slot<ParkingRegistryEntity>()
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.ENTRY,
            entryTime = LocalDateTime.now()
        )

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(request.licensePlate) } returns null
        every { spotRepository.findByIsTaken(false) } returns mockSpotList()
        every { spotRepository.countByIsTaken(true) } returns spotsTakenCount

        every { parkingRegistryRepository.save(capture(registrySlot)) } returns mockk()
        every { spotRepository.save(any()) } returns mockk()

        parkingRegistryService.register(request)

        assertEquals(BigDecimal(expectedDiscount), registrySlot.captured.percentPriceDiscount)
        assertEquals(BigDecimal(expectedIncrease), registrySlot.captured.percentPriceIncrease)

    }

    @ParameterizedTest
    @CsvSource(value = ["30,0", "60,10", "90,20"])
    fun `should successfully exit park a car and calculate price`(minutes: Long, expectedPriceValue: String) {
        val time = LocalDateTime.now()
        val registrySlot = slot<ParkingRegistryEntity>()
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.EXIT,
            exitTime = time
        )

        val register = mockParkingRegistry(parameterDate = time.minusMinutes(minutes))

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(request.licensePlate) } returns register

        every { parkingRegistryRepository.save(capture(registrySlot)) } returns mockk()
        every { spotRepository.save(any()) } returns mockk()

        val result = parkingRegistryService.register(request)

        assertEquals(request.exitTime, registrySlot.captured.datEnd)
        assertEquals(EventType.EXIT, result.eventType)
        assertEquals(BigDecimal(expectedPriceValue), registrySlot.captured.revenue)
    }

    @Test
    fun `should throws IllegalArgumentException when entryTime is null for EventType ENTRY`() {
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.ENTRY
        )

        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals("Entry time is required for parking", exception.message)
    }

    @Test
    fun `should throws IllegalArgumentException when exitTime is null for EventType EXIT`() {
        val request = WebHookRequest(
            licensePlate = "ABC-1234",
            eventType = EventType.EXIT
        )

        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals("Exit time is required for exit", exception.message)
    }

    @Test
    fun `should throws IllegalArgumentException when LicensePlate is already Park`() {
        val licensePlate = "ASD-1378"
        val request = WebHookRequest(
            licensePlate = licensePlate,
            eventType = EventType.ENTRY,
            entryTime = LocalDateTime.now()
        )

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(licensePlate) } returns mockParkingRegistry()

        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals("License plate=$licensePlate is already parked", exception.message)
    }

    @Test
    fun `should throws FullGarageException when there is no spots available`() {
        val licensePlate = "ASD-1378"
        val request = WebHookRequest(
            licensePlate =  "ASD-1378",
            eventType = EventType.ENTRY,
            entryTime = LocalDateTime.now()
        )

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(licensePlate) } returns null
        every { spotRepository.findByIsTaken(false) } returns emptyList()

        val exception = Assertions.assertThrows(FullGarageException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals("Garage is full, no available spots", exception.message)
    }

    @Test
    fun `should throws NotFoundException when car is not parked for EventType EXIT`() {
        val licensePlate = "ABC-1234"
        val request = WebHookRequest(
            licensePlate = licensePlate,
            eventType = EventType.EXIT,
            exitTime = LocalDateTime.now()
        )

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(licensePlate) } returns null

        val exception = Assertions.assertThrows(NotFoundException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals("License plate=$licensePlate not found", exception.message)
    }

    @Test
    fun `should throws IllegalArgumentException when exit time is before entry time`() {
        val licensePlate = "ABC-1234"
        val request = WebHookRequest(
            licensePlate = licensePlate,
            eventType = EventType.EXIT,
            exitTime = LocalDateTime.of(2024, 6, 1, 12, 0)
        )

        val register = mockParkingRegistry()

        every { parkingRegistryRepository.findByLicensePlateAndDatEndIsNull(licensePlate) } returns register
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals(
            "Exit time cannot be before entry time, exitTime=${request.exitTime}, entryTime=${register.datStart}",
            exception.message
        )
    }

    @Test
    fun `should throws InvalidArgumentException when event Type is invalid`() {
        val licensePlate = "ABC-1234"
        val request = WebHookRequest(
            licensePlate = licensePlate,
            eventType = EventType.PARKED
        )

        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            parkingRegistryService.register(request)
        }

        assertEquals("Invalid event type: ${request.eventType}", exception.message)
    }


    private fun mockParkingRegistry(parameterDate: LocalDateTime = LocalDateTime.now()): ParkingRegistryEntity {
        return ParkingRegistryEntity(
            spot = SpotEntity(
                sector = SectorEntity(
                    name = "",
                    garage = GarageEntity(name = "", sectors = listOf()),
                    basePrice = BigDecimal.TEN,
                    maxCapacity = 0

                ), latitude = 0, longitude = 0, isTaken = true

            ),
            datStart = parameterDate,
            datEnd = null,
            licensePlate = "",
            revenue = BigDecimal.ZERO,
            percentPriceDiscount = BigDecimal.ZERO,
            percentPriceIncrease = BigDecimal.ZERO
        )
    }

    private fun mockSpotList(): List<SpotEntity> {
        val list = mutableListOf<SpotEntity>()
        val garage = GarageEntity("Garage", listOf())

        for (i in 'a'..'d') {
            val sector = SectorEntity(
                name = i.toString().uppercase(),
                garage = garage,
                basePrice = BigDecimal.TEN,
                maxCapacity = 0

            )
            for (j in 1..25) {
                list.add(
                    SpotEntity(
                        sector = sector,
                        latitude = j.toLong(),
                        longitude = j.toLong(),
                        isTaken = false
                    )
                )
            }
        }
        return list
    }
}
