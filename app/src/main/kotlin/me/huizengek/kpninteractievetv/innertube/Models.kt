package me.huizengek.kpninteractievetv.innertube

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class Channel(
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

data class Stream(val startTime: Long, val url: String, val licenseAcquisitionURL: String? = null)
