package me.huizengek.kpninteractievetv.util

import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

fun Window.setKeepScreenOn() = addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
fun Window.unsetKeepScreenOn() = clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

@Composable
fun KeepScreenOn(on: Boolean) {
    val context = LocalContext.current

    DisposableEffect(context, on) {
        val window = context.findActivity().window
        if (on) window.setKeepScreenOn() else window.unsetKeepScreenOn()
        onDispose { window.unsetKeepScreenOn() }
    }
}
