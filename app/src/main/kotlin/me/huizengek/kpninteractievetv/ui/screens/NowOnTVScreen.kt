package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.requests.getChannels
import me.huizengek.kpninteractievetv.channelState
import me.huizengek.kpninteractievetv.util.focusOnLaunch
import me.huizengek.kpninteractievetv.util.focusRequesters

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NowOnTVScreen() {
    val isLoggedIn by KpnClient.isLoggedIn.collectAsState()

    var loadingChannels: Result<List<ChannelContainer>?>? by remember { mutableStateOf(null) }
    var retrying by remember { mutableStateOf(false) }

    var retryCount by rememberSaveable { mutableIntStateOf(1) }

    LaunchedEffect(KpnClient.isLoggedIn, retrying) {
        withContext(Dispatchers.IO) {
            if ((loadingChannels == null && isLoggedIn) || retrying) {
                loadingChannels = KpnClient.getChannels()
                if (retrying) retrying = false
            }
        }
    }

    if (retrying) Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Opnieuw proberen...")
        Spacer(modifier = Modifier.height(12.dp))
        CircularProgressIndicator()
    } else loadingChannels?.getOrNull()?.let { channels ->
        BoxWithConstraints {
            val requesters = focusRequesters()

            var wasFocused by rememberSaveable { mutableStateOf(false) }
            var lastFocused: FocusRequester? by remember { mutableStateOf(null) }

            TvLazyVerticalGrid(
                columns = TvGridCells.Fixed(5),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .height(maxHeight)
                    .onFocusChanged {
                        val isFocusedNow = it.isFocused || it.hasFocus
                        if (!wasFocused && isFocusedNow) (lastFocused
                            ?: requesters[0]).requestFocus()
                        wasFocused = isFocusedNow
                    },
                pivotOffsets = PivotOffsets(parentFraction = 0.2f)
            ) {
                itemsIndexed(
                    items = channels,
                    key = { _, channel -> channel.metadata.id }
                ) { i, channel ->
                    ChannelItem(
                        channel = channel,
                        onClick = {
                            channelState.update { channel }
                        },
                        focusRequester = requesters[i],
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) lastFocused = requesters[i]
                        }
                    )
                }
            }
        }
    } ?: loadingChannels?.exceptionOrNull()?.let {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    retrying = true
                    retryCount++
                },
                modifier = Modifier.focusOnLaunch(retrying)
            ) {
                Text(text = "Probeer opnieuw")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Er is een fout opgetreden, probeer het later nogmaals.")

            if (retryCount >= 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Het laden is nu $retryCount keer mislukt, probeer anders opnieuw in te loggen")
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = { KpnClient.logout() }) { Text(text = "Log uit") }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelItem(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    channel: ChannelContainer,
    onClick: () -> Unit
) {
    val focusColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .padding(top = 12.dp)
                .aspectRatio(16f / 9f)
                .fillMaxSize()
                .focusRequester(focusRequester = focusRequester),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            glow = ClickableSurfaceDefaults.glow(
                focusedGlow = Glow(
                    elevation = 4.dp,
                    elevationColor = focusColor
                )
            ),
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(
                    BorderStroke(
                        8.dp,
                        focusColor
                    )
                )
            )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://images.tv.kpn.com/logo/${channel.metadata.externalId}/256.png")
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(0.8f)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = channel.metadata.name,
            style = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
