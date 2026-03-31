package dev.stormgui.slim_garage.domain.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import dev.stormgui.slim_garage.domain.enums.EventType
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WebhookResponse(
    val licensePlate: String,
    val entryTime: LocalDateTime? = null,
    val exitTime: LocalDateTime? = null,
    val eventType: EventType,
    val lat: Long? = null,
    val lng: Long? = null
)

data class WebHookRequest(
    @JsonProperty(value = "license_plate")
    val licensePlate: String,
    @JsonProperty(value = "entry_time")
    val entryTime: LocalDateTime? = null,
    @JsonProperty(value = "exit_time")
    val exitTime: LocalDateTime? = null,
    @JsonProperty(value = "event_type")
    val eventType: EventType
)
