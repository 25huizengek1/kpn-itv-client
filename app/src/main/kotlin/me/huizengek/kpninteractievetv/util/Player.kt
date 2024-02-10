package me.huizengek.kpninteractievetv.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.media3.common.Player

@Composable
inline fun Player.Listener(crossinline provider: () -> Player.Listener) {
    DisposableEffect(this) {
        val listener = provider()
        addListener(listener)
        onDispose { removeListener(listener) }
    }
}

@Composable
fun Player.playbackState(): MutableIntState {
    val state = remember { mutableIntStateOf(playbackState) }
    Listener {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                state.intValue = playbackState
            }
        }
    }
    return state
}
