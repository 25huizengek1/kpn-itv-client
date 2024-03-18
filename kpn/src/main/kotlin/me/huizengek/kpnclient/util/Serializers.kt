package me.huizengek.kpnclient.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val tz = TimeZone.UTC

object MillisSerializer : KSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor("MillisDateTime", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder) =
        Instant.fromEpochMilliseconds(decoder.decodeLong()).toLocalDateTime(tz)

    override fun serialize(encoder: Encoder, value: LocalDateTime) =
        encoder.encodeLong(value.toInstant(tz).toEpochMilliseconds())
}

typealias MillisLocalDateTime =
    @Serializable(with = MillisSerializer::class)
    LocalDateTime

object SecondsDurationSerializer : KSerializer<Duration> {
    override val descriptor = PrimitiveSerialDescriptor("SecondsDuration", PrimitiveKind.LONG)
    override fun deserialize(decoder: Decoder) = decoder.decodeLong().seconds
    override fun serialize(encoder: Encoder, value: Duration) = encoder.encodeLong(value.inWholeSeconds)
}

typealias SecondsDuration =
    @Serializable(with = SecondsDurationSerializer::class)
    Duration
