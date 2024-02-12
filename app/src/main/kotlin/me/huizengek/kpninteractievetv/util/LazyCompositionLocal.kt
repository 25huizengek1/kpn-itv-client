package me.huizengek.kpninteractievetv.util

import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import me.huizengek.kpninteractievetv.GlobalPreferencesHolder

object CompositionLocalPreferences : GlobalPreferencesHolder() {
    var warn by boolean(true)
}

inline fun <reified T> lateinitCompositionLocalOf() = compositionLocalOf<T> {
    if (CompositionLocalPreferences.warn) {
        val tag = "LazyCompositionLocal"
        Log.w(tag, "Usage of lateinit composition local before initialization!")
        Log.w(tag, "This is almost always a bug!")
        Log.w(tag, "Referenced class: ${T::class}")
        Log.w(tag, "")
        Log.w(tag, "If this was intentional, you can disable these notifications using:")
        Log.w(tag, "    CompositionLocalPreferences.warn = false")
    }
    error("Lateinit composition local not initialized yet!")
}
