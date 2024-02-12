package me.huizengek.kpnclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.huizengek.kpnclient.requests.login
import me.huizengek.kpnclient.util.CookieJar
import kotlin.time.Duration.Companion.minutes

internal val json = Json {
    isLenient = true
    prettyPrint = true
    ignoreUnknownKeys = true
    encodeDefaults = true
}

object KpnClient {
    internal val httpClient by lazy {
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

    internal val jar = CookieJar()
    internal val isLoggedInState = MutableStateFlow(false)
    val isLoggedIn = isLoggedInState.asStateFlow()

    private var lastSet: Long? = null
    internal var channelCache: List<ChannelContainer>? = null
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

    suspend fun use(
        session: SessionInfo,
        deviceId: String
    ) = login(
        username = session.username,
        password = session.password,
        deviceId = deviceId
    )

    fun logout() = isLoggedInState.update { false }
}

@Serializable
data class SessionInfo(
    val username: String,
    val password: String,
    val displayName: String? = null
)
