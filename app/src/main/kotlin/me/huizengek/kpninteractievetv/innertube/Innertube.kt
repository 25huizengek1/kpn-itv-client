package me.huizengek.kpninteractievetv.innertube

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import me.huizengek.kpninteractievetv.models.Session
import me.huizengek.kpninteractievetv.preferences.KpnPreferences
import me.huizengek.kpninteractievetv.util.div
import me.huizengek.kpninteractievetv.util.plusAssign
import kotlin.time.Duration.Companion.minutes

val json = Json {
    isLenient = true
    prettyPrint = true
    ignoreUnknownKeys = true
    encodeDefaults = true
}

object Innertube {
    private val httpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }
            defaultRequest {
                kpnHeaders()
                jar.use()

                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
        }
    }

    private val jar = CookieJar()
    var isLoggedIn by mutableStateOf(false)
        private set

    private var lastSet: Long? = null
    private var channelCache: List<Channel>? = null
        get() {
            if ((lastSet ?: Long.MAX_VALUE) <
                (System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
            ) field = null
            return field
        }
        set(value) {
            lastSet = System.currentTimeMillis()
            field = value
        }

    suspend fun login(
        username: String,
        password: String,
        info: AuthInfo = AuthInfo(
            deviceInfo = AuthInfo.DeviceInfo(deviceId = KpnPreferences.deviceId),
            credentials = AuthInfo.Credentials(
                username = username,
                password = password
            )
        )
    ) = (runCatching {
        jar.clear()
        jar += Cookie("_ga", "GA1.2.166396111.1682809839")
        jar += Cookie("_gat", "1")

        httpClient.post(sessions) {
            setBody(mapOf("credentialsExtAuth" to info))
        }.save(jar).let {
            it.status.isSuccess() && it.body<JsonElement>().jsonObject["errorDescription"]
                ?.jsonPrimitive?.content?.contains("200") == true
        }
    }.getOrNull() == true).also { isLoggedIn = it }

    suspend fun use(session: Session) = login(session.username, session.password)

    fun logout() {
        isLoggedIn = false
    }

    suspend fun getChannels() = channelCache?.let { Result.success(it) } ?: runCatching {
        (httpClient.get(liveChannels) {
            "orderBy" += "orderId"
            "sortOrder" += "asc"
            "from" += "0"
            "to" += "999"
            "dfilter_channels" += "subscription"
            "filter_isAdult" += "false"
        }.save(jar).body<JsonElement>() / "resultObj" / "containers").jsonArray.map {
            Channel(
                assets = json.decodeFromJsonElement<List<Channel.Asset>>(it / "assets"),
                metadata = json.decodeFromJsonElement<Channel.Metadata>(it / "metadata")
            )
        }
    }.onSuccess { channelCache = it }

    suspend fun getStream(channel: Channel) = runCatching {
        val channelId = channel.metadata.id
        val assetId = channel.bestAsset.id
        val deviceId = KpnPreferences.deviceId

        httpClient.get(dshToken) {
            "deviceId" += deviceId
            accept(ContentType.Any)
        }.save(jar)

        val data = httpClient.get(channels(channelId, assetId)) {
            "deviceId" += deviceId
            "profile" += profile
            accept(ContentType.Any)
        }.save(jar).body<JsonElement>()

        val sources = data / "resultObj" / "src" / "sources"
        val url = (sources / "src").jsonPrimitive.content
        val time = (data / "systemTime").jsonPrimitive.long
        val licenseAcquisitionURL = sources.jsonObject["contentProtection"]?.jsonObject?.values
            ?.firstOrNull()?.jsonObject?.get("licenseAcquisitionURL")?.jsonPrimitive?.content

        Stream(
            startTime = time,
            url = url,
            licenseAcquisitionURL = licenseAcquisitionURL
        )
    }
}