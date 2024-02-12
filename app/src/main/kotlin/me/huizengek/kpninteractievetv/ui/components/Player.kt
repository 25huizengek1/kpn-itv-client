package me.huizengek.kpninteractievetv.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView

@Composable
fun Player(
    player: Player,
    modifier: Modifier = Modifier
) = AndroidView(
    modifier = modifier,
    factory = { context ->
        PlayerView(context).also {
            it.player = player
        }
    }
)
