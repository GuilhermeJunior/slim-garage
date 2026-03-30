package dev.stormgui.slim_garage.domain.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import dev.stormgui.slim_garage.domain.enums.EventType
import java.time.LocalDateTime

data class WebhookResponse(
    val licensePlate: String,
    val entryTime: LocalDateTime,
    val eventType: EventType,
    val lat: Long? = null,
    val lng: Long? = null
)

data class WebHookRequest(
    @JsonProperty(value = "license_plate")
    val licensePlate: String,
    @JsonProperty(value = "entry_time")
    val entryTime: LocalDateTime,
    @JsonProperty(value = "exit_time")
    val exitTime: LocalDateTime,
    @JsonProperty(value = "event_type")
    val eventType: EventType
)
