package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.requests.getChannels
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.channelState
import me.huizengek.kpninteractievetv.ui.items.ChannelItem
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
                        if (!wasFocused && isFocusedNow)
                            (lastFocused ?: requesters[0]).requestFocus()
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
                        onClick = { channelState.update { channel } },
                        focusRequester = requesters[i],
                        modifier = Modifier
                            .onFocusChanged {
                                if (it.isFocused) lastFocused = requesters[i]
                            }
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
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
                Text(text = stringResource(R.string.try_again))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.error_unknown))

            if (retryCount >= 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.retry_try_logout, retryCount))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = { KpnClient.logout() }) {
                    Text(text = stringResource(R.string.logout))
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
