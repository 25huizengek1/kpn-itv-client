package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonScale
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpninteractievetv.Database
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.preferences.AppPreferences

// TODO refactor
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val sessions by Database.sessions().collectAsState(initial = listOf())
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }

    Column {
        Text(
            text = "Beschikbare accounts/sessies (klik om te verwijderen)",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(start = 48.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn {
            items(sessions) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 48.dp),
                    onClick = {
                        coroutineScope.launch {
                            Database.delete(it)
                        }
                    },
                    scale = ClickableSurfaceScale.None,
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.person),
                            contentDescription = null,
                            modifier = Modifier.padding(all = 16.dp)
                        )

                        Text(text = it.displayName ?: it.username)
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { KpnClient.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 4.dp),
                    scale = ButtonScale.None
                ) {
                    Text(text = "Nieuwe sessie")
                }
            }
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 48.dp),
                    onClick = { AppPreferences.darkTheme = !AppPreferences.darkTheme },
                    scale = ClickableSurfaceScale.None,
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Donker thema")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = AppPreferences.darkTheme,
                            onCheckedChange = { AppPreferences.darkTheme = it },
                            enabled = false
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 48.dp),
                    onClick = { AppPreferences.keepScreenOn = !AppPreferences.keepScreenOn },
                    scale = ClickableSurfaceScale.None,
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Houd scherm aan")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = AppPreferences.keepScreenOn,
                            onCheckedChange = { AppPreferences.keepScreenOn = it },
                            enabled = false
                        )
                    }
                }
            }
        }
    }
}
