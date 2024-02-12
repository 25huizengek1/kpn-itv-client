package me.huizengek.kpninteractievetv.ui.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.models.Session

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SessionItem(
    session: Session,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = ItemContainer(
    modifier = modifier,
    scale = ClickableSurfaceScale.None,
    onClick = onClick
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.person),
            contentDescription = null,
            modifier = Modifier.padding(all = 16.dp)
        )

        Text(text = session.displayName ?: session.username)
    }
}
