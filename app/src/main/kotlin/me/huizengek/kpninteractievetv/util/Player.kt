package me.huizengek.kpninteractievetv.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.IntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.media3.common.Player

@Suppress("LambdaParameterInRestartableEffect") // inline
@Composable
inline fun Player.DisposableListener(crossinline provider: () -> Player.Listener) {
    DisposableEffect(this) {
        val listener = provider()
        addListener(listener)
        onDispose { removeListener(listener) }
    }
}

@Composable
fun Player.playbackState(): IntState {
    val state = remember { mutableIntStateOf(playbackState) }

    DisposableListener {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                state.intValue = playbackState
            }
        }
    }

    return state
}
