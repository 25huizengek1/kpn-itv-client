package me.huizengek.kpninteractievetv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import me.huizengek.kpninteractievetv.preferences.PreferencesHolder

object DependencyGraph {
    lateinit var application: InteractieveTVApplication
    var player: Player? by mutableStateOf(null)

    context(InteractieveTVApplication)
    internal fun init() {
        application = this@InteractieveTVApplication
        DatabaseAccessor.init()
    }
}

open class GlobalPreferencesHolder : PreferencesHolder(DependencyGraph.application, "preferences")