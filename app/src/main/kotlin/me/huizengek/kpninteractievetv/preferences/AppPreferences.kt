package me.huizengek.kpninteractievetv.preferences

import me.huizengek.kpninteractievetv.GlobalPreferencesHolder

object AppPreferences : GlobalPreferencesHolder() {
    var darkTheme by boolean(true)
}