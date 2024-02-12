package me.huizengek.kpninteractievetv.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpninteractievetv.util.semiBold

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelItem(
    modifier: Modifier = Modifier,
    channel: ChannelContainer,
    focusRequester: FocusRequester,
    onClick: () -> Unit
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Spacer(modifier = Modifier.height(4.dp))

    ItemContainer(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(16f / 9f)
            .fillMaxSize(),
        focusRequester = focusRequester
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
        text = channel.metadata.name + "\n",
        style = LocalTextStyle.current.semiBold,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}
