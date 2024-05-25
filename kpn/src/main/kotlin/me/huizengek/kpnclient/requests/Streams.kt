package me.huizengek.kpnclient.requests

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import kotlinx.datetime.LocalDateTime
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.PROFILE
import me.huizengek.kpnclient.QueryResponse
import me.huizengek.kpnclient.ROOT
import me.huizengek.kpnclient.StreamResponse
import me.huizengek.kpnclient.util.plusAssign
import me.huizengek.kpnclient.util.runCatchingCancellable
import me.huizengek.kpnclient.util.save

suspend fun KpnClient.refreshDsh(deviceId: String) = runCatchingCancellable {
    httpClient.get("$ROOT/USER/DSHTOKEN") {
        "deviceId" += deviceId
        accept(ContentType.Any)
    }.save(jar)
}

suspend fun KpnClient.getStream(
    channel: ChannelContainer,
    deviceId: String,
    asset: ChannelContainer.Asset = channel.bestAsset
) = runCatchingCancellable {
    refreshDsh(deviceId)

    val stream = httpClient.get("$ROOT/CONTENT/VIDEOURL/LIVE/${channel.metadata.id}/${asset.id}") {
        "deviceId" += deviceId
        "profile" += PROFILE
        accept(ContentType.Any)
    }
        .save(jar)
        .body<QueryResponse<StreamResponse>>()
        .takeIf { it.code == "OK" } ?: return@runCatchingCancellable null

    Stream(
        startTime = stream.time,
        url = stream.value.url,
        licenseAcquisitionUrl = stream.value.licenseAcquisitionUrl
    )
}

data class Stream(
    val startTime: LocalDateTime,
    val url: String?,
    val licenseAcquisitionUrl: String?
)
