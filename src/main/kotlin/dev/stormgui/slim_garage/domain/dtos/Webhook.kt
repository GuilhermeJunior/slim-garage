package dev.stormgui.slim_garage.domain.dtos

import dev.stormgui.slim_garage.domain.enums.EventType
import java.time.LocalDateTime

data class WebhookResponse(
    val licensePlate: String,
    val entryTime: LocalDateTime,
    val eventType: EventType
)

data class WebhookEntry(
    val licensePlate: String,
    val entryTime: LocalDateTime,
    val eventType: EventType
)