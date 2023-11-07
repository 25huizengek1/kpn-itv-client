package me.huizengek.kpninteractievetv.ui.screens

import android.net.Uri
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.LocalPlayer
import me.huizengek.kpninteractievetv.util.findActivity
import me.huizengek.kpninteractievetv.util.focusOnLaunch
import me.huizengek.kpninteractievetv.innertube.Channel
import me.huizengek.kpninteractievetv.innertube.Innertube
import me.huizengek.kpninteractievetv.innertube.Stream

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun WatchScreen(channel: Channel) {
    val navigator = LocalNavigator.current
    val player = LocalPlayer.current
    val context = LocalContext.current

    var streamResult: Result<Stream>? by remember { mutableStateOf(null) }
    val mediaItem by remember {
        derivedStateOf {
            streamResult?.getOrNull()?.let { stream ->
                (channel to stream).toMediaItem()
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            streamResult = Innertube.getStream(channel)
        }
    }

    LaunchedEffect(mediaItem, player) {
        mediaItem?.let { item ->
            player?.setMediaItem(item)
            player?.prepare()
            if (player?.playWhenReady == false) player.playWhenReady = true
            player?.play()
        }
    }

    BackHandler {
        player?.pause()
        navigator.navigateUp()
    }

    DisposableEffect(Unit) {
        context.findActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { context.findActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    streamResult?.getOrNull()?.let {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).also {
                    it.player = player
                }
            }
        )
    } ?: streamResult?.exceptionOrNull()?.let {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Er is iets misgegaan. Probeer het later opnieuw")
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navigator.navigateUp() },
                modifier = Modifier.focusOnLaunch()
            ) {
                Text(text = "Ga terug")
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

fun Pair<Channel, Stream>.toMediaItem(): MediaItem {
    val (channel, stream) = this

    return MediaItem.Builder()
        .setUri(stream.url)
        .setLiveConfiguration(
            MediaItem.LiveConfiguration.Builder()
                .setMaxPlaybackSpeed(1.02f)
                .build()
        ).let {
            if (stream.licenseAcquisitionURL != null) it.setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(stream.licenseAcquisitionURL)
                    .setMultiSession(true)
                    .build()
            ) else it
        }.setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(channel.metadata.name)
                .setSubtitle(channel.metadata.name)
                .setDisplayTitle(channel.metadata.name)
                .setArtworkUri(Uri.parse("https://images.tv.kpn.com/logo/${channel.metadata.externalId}/256.png"))
                .build()
        ).build()
}