package me.huizengek.kpninteractievetv.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.requests.Stream
import me.huizengek.kpnclient.requests.getStream
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.LocalPlayer
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.channelState
import me.huizengek.kpninteractievetv.preferences.AppPreferences
import me.huizengek.kpninteractievetv.preferences.KpnPreferences
import me.huizengek.kpninteractievetv.ui.components.Player
import me.huizengek.kpninteractievetv.util.KeepScreenOn
import me.huizengek.kpninteractievetv.util.focusOnLaunch
import me.huizengek.kpninteractievetv.util.playbackState

@Destination
@Composable
fun WatchScreen() {
    val navigator = LocalNavigator.current
    val player = LocalPlayer.current
    val channel by channelState.collectAsState()

    LaunchedEffect(channel) {
        if (channel == null) navigator.navigateUp()
    }

    var streamResult: Result<Stream?>? by remember { mutableStateOf(null) }
    val mediaItem by remember(channel, streamResult) {
        derivedStateOf {
            val currentChannel = channel ?: return@derivedStateOf null
            streamResult?.getOrNull()?.let { stream ->
                (currentChannel to stream).toMediaItem()
            }
        }
    }

    player ?: return

    val playbackState by player.playbackState()

    LaunchedEffect(channel) {
        withContext(Dispatchers.IO) {
            streamResult = channel?.let {
                KpnClient.getStream(
                    channel = it,
                    deviceId = KpnPreferences.deviceId
                )
            }
        }
    }

    LaunchedEffect(mediaItem, player) {
        mediaItem?.let { item ->
            player.setMediaItem(item, true)
            player.play()
            player.prepare()
            player.seekToDefaultPosition()
        }
    }

    BackHandler {
        player.pause()
        navigator.navigateUp()
    }

    KeepScreenOn(on = AppPreferences.keepScreenOn)

    streamResult?.getOrNull()?.let {
        Player(
            player = player,
            modifier = Modifier.fillMaxSize()
        )
    } ?: streamResult?.exceptionOrNull()?.let {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.error_unknown))
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navigator.navigateUp() },
                modifier = Modifier.focusOnLaunch()
            ) {
                Text(text = stringResource(R.string.go_back))
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = streamResult == null || playbackState == Player.STATE_BUFFERING) {
            CircularProgressIndicator()
        }
    }
}

fun Pair<ChannelContainer, Stream>.toMediaItem(): MediaItem {
    val (channel, stream) = this
    val name = channel.metadata.name

    return MediaItem.Builder()
        .setUri(stream.url)
        .setLiveConfiguration(
            MediaItem.LiveConfiguration.Builder()
                .setMaxPlaybackSpeed(1.02f)
                .build()
        ).let {
            if (stream.licenseAcquisitionUrl != null) it.setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(stream.licenseAcquisitionUrl)
                    .setMultiSession(true)
                    .build()
            ) else it
        }.setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setSubtitle(name)
                .setDisplayTitle(name)
                .setArtworkUri(Uri.parse("https://images.tv.kpn.com/logo/${channel.metadata.externalId}/256.png"))
                .build()
        ).build()
}
