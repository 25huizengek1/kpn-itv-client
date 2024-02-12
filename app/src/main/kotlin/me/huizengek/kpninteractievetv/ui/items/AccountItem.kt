package me.huizengek.kpninteractievetv.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.models.Session

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AccountItem(
    session: Session?,
    width: Dp = 96.dp,
    onClick: () -> Unit
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(width)
) {
    ItemContainer(onClick = onClick) {
        Icon(
            painter = painterResource(if (session == null) R.drawable.add else R.drawable.person),
            contentDescription = null,
            modifier = Modifier
                .padding(12.dp)
                .clip(CircleShape)
                .size(40.dp)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = session?.displayName ?: session?.username ?: "Toevoegen",
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}