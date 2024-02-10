package me.huizengek.kpnclient.requests

import io.ktor.client.call.body
import io.ktor.client.request.get
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpnclient.ContainerResponse
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.QueryResponse
import me.huizengek.kpnclient.SortOrder
import me.huizengek.kpnclient.root
import me.huizengek.kpnclient.util.plusAssign
import me.huizengek.kpnclient.util.runCatchingCancellable
import me.huizengek.kpnclient.util.save

suspend fun KpnClient.getChannels(
    order: SortOrder = SortOrder.Ascending,
    from: Int = 0,
    to: Int = 999,
    filterSubscribed: Boolean = true,
    filterNoAdult: Boolean = false
) = channelCache?.let { Result.success(it) } ?: runCatchingCancellable {
    val channels = httpClient
        .get("$root/TRAY/LIVECHANNELS") {
            "orderBy" += "orderId"
            "sortOrder" += order.value
            "from" += from
            "to" += to
            "filter_isAdult" += filterNoAdult
            if (filterNoAdult) "filter_excludedGenres" += "Erotiek"
            if (filterSubscribed) "dfilter_channels" += "subscription"
        }
        .save(jar)
        .body<QueryResponse<ContainerResponse<ChannelContainer>>>()
        .takeIf { it.code == "OK" }
        ?.value
        ?.containers

    channelCache = channels
    channels
}
