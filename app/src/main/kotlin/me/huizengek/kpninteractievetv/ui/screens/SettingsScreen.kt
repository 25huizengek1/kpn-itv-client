package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonScale
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpninteractievetv.Database
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.preferences.AppPreferences
import me.huizengek.kpninteractievetv.ui.items.ItemContainer
import me.huizengek.kpninteractievetv.ui.items.SessionItem
import me.huizengek.kpninteractievetv.util.semiBold

@Suppress("ModifierMissing")
@Composable
fun SettingsScreen() = Column {
    val sessions by Database.sessions().collectAsState(initial = listOf())
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }

    Text(
        text = stringResource(R.string.current_sessions_header),
        style = MaterialTheme.typography.labelLarge.semiBold,
        modifier = Modifier.padding(start = 48.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))

    LazyColumn {
        items(
            items = sessions,
            key = { it.id }
        ) {
            SessionItem(
                session = it,
                onClick = {
                    coroutineScope.launch {
                        Database.delete(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 48.dp)
            )
        }
        item {
            OutlinedButton(
                onClick = KpnClient::logout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 4.dp),
                scale = ButtonScale.None
            ) {
                Text(text = stringResource(R.string.new_session))
            }
        }
        item {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
        }
        item {
            SwitchSettingsEntry(
                label = stringResource(R.string.dark_theme),
                checked = AppPreferences.darkTheme,
                onToggle = { AppPreferences.darkTheme = !AppPreferences.darkTheme }
            )
        }
        item {
            SwitchSettingsEntry(
                label = stringResource(R.string.keep_screen_on),
                checked = AppPreferences.keepScreenOn,
                onToggle = { AppPreferences.keepScreenOn = !AppPreferences.keepScreenOn }
            )
        }
    }
}

@Composable
fun SettingsEntry(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) = ItemContainer(
    modifier = modifier
        .padding(bottom = 8.dp)
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 48.dp),
    onClick = onClick,
    scale = ClickableSurfaceScale.None
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp),
        content = content
    )
}

@Composable
fun SwitchSettingsEntry(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) = SettingsEntry(
    onClick = onToggle,
    modifier = modifier
) {
    Text(text = label)
    Spacer(modifier = Modifier.weight(1f))
    Switch(
        checked = checked,
        onCheckedChange = { onToggle() }
    )
}
