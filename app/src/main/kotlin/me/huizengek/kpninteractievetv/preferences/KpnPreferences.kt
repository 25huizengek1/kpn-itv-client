package me.huizengek.kpninteractievetv.preferences

import me.huizengek.kpninteractievetv.GlobalPreferencesHolder
import me.huizengek.kpnclient.util.sha256
import kotlin.random.Random

private fun randomDeviceId(): String {
    val number = Random.nextLong(90000) + 10000
    val timestamp = System.currentTimeMillis()
    return "$number$timestamp".encodeToByteArray().sha256
}

object KpnPreferences : GlobalPreferencesHolder() {
    private var nullableDeviceId by nullableString(null)
    val deviceId get() = nullableDeviceId ?: randomDeviceId().also { nullableDeviceId = it }
}
