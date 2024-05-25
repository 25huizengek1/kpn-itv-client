package me.huizengek.kpnclient.requests

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Cookie
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import me.huizengek.kpnclient.AuthInfo
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.QueryResponse
import me.huizengek.kpnclient.ROOT
import me.huizengek.kpnclient.util.runCatchingCancellable
import me.huizengek.kpnclient.util.save

suspend fun KpnClient.login(
    username: String,
    password: String,
    deviceId: String
): Boolean {
    val loggedIn = runCatchingCancellable {
        jar.clear()
        jar += Cookie(name = "_ga", value = "GA1.2.166396111.1682809839")
        jar += Cookie(name = "_gat", value = "1")

        httpClient.post("$ROOT/USER/SESSIONS") {
            setBody(
                CredentialsBody(
                    credentials = AuthInfo(
                        deviceInfo = AuthInfo.DeviceInfo(deviceId = deviceId),
                        credentials = AuthInfo.Credentials(
                            username = username,
                            password = password
                        )
                    )
                )
            )
        }.save(jar).let {
            it.status.isSuccess() && it.body<QueryResponse<JsonElement>>().code == "OK"
        }
    }?.getOrNull() == true

    isLoggedInState.update { loggedIn }

    return loggedIn
}

@Serializable
data class CredentialsBody(
    @SerialName("credentialsExtAuth")
    val credentials: AuthInfo
)
