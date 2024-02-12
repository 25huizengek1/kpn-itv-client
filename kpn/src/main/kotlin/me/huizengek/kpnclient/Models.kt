package me.huizengek.kpnclient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import me.huizengek.kpnclient.util.MillisLocalDateTime
import me.huizengek.kpnclient.util.SecondsDuration
import me.huizengek.kpnclient.util.div

@JvmInline
value class SortOrder private constructor(internal val value: String) {
    companion object {
        val Ascending = SortOrder(value = "asc")
        val Descending = SortOrder(value = "desc")
    }
}

@Serializable
data class AuthInfo(
    val deviceInfo: DeviceInfo,
    val credentials: Credentials,
    val remember: String = "Y"
) {
    @Serializable
    data class DeviceInfo(
        val deviceId: String,
        val deviceIdType: String = "DEVICEID",
        val deviceType: String = "PCTV",
        val deviceVendor: String = "CHROME",
        @SerialName("deviceModel")
        val deviceFirmVersion: String = "112.0.0.0",
        @SerialName("deviceFirmVersion")
        val deviceModel: String = "Windows",
        val appVersion: String = "10"
    )

    @Serializable
    data class Credentials(
        val loginType: String = "UsernamePassword",
        val username: String,
        val password: String,
        val appId: String = "KPN"
    )
}

@Serializable
data class QueryResponse<T : Any>(
    @SerialName("errorDescription")
    val error: String,
    val message: String,
    @SerialName("resultCode")
    val code: String,
    @SerialName("systemTime")
    val time: MillisLocalDateTime,
    @SerialName("resultObj")
    val value: T
)

@Serializable
data class ContainerResponse<T : Any>(
    val containers: List<T>,
    val total: Long
)

@Serializable
data class ChannelReference(
    @SerialName("channelId")
    val id: Int,
    @SerialName("channelName")
    val name: String,
    @SerialName("externalChannelId")
    val externalId: String,
    @SerialName("isAdult")
    val adult: Boolean? = null,
    val type: String
)

@Serializable
data class ChannelContainer(
    val id: String,
    val assets: List<Asset>,
    val metadata: Metadata
) {
    val bestAsset get() = assets.maxBy { it.metadata.ranking }

    @Serializable
    data class Asset(
        @SerialName("assetId")
        val id: Int,
        val metadata: Metadata
    ) {
        @Serializable
        data class Metadata(
            val ranking: Int
        )
    }

    @Serializable
    data class Metadata(
        @SerialName("channelId")
        val id: Int,
        val externalId: String,
        @SerialName("orderId")
        val order: Int,
        @SerialName("channelName")
        val name: String
    )
}

@Serializable
data class StreamResponse(
    val token: String,
    @SerialName("src")
    private val source: JsonElement
) {
    val licenseAcquisitionUrl by lazy {
        runCatching {
            (source / "sources" / "contentProtection" / "widevine" / "licenseAcquisitionURL").jsonPrimitive.content
        }.getOrNull()
    }
    val url by lazy {
        runCatching {
            (source / "sources" / "src").jsonPrimitive.content
        }.getOrNull()
    }
}

// TO DO
@Serializable
data class ContentContainer(
    val id: String,
    val metadata: Metadata,
    val channel: ChannelReference
) {
    @Serializable
    data class Metadata(
        @SerialName("airingStartTime")
        val start: MillisLocalDateTime,
        @SerialName("airingEndTime")
        val end: MillisLocalDateTime,
        val contentId: Int,
        val contentOptions: List<String>,
        val duration: SecondsDuration,
        val season: Int,
        @SerialName("episodeNumber")
        val episode: Int,
        val externalId: String,
        val genres: List<String>,
        val shortDescription: String,
        val longDescription: String,
        @SerialName("pictureUrl")
        val thumbnailId: String,
        val presenters: List<String>
    )

    fun thumbnail(
        width: Int,
        height: Int,
        blurred: Boolean = false
    ) = "https://images.tv.kpn.com/epg/${metadata.thumbnailId}/${width}x$height.jpg?blurred=$blurred"
}
