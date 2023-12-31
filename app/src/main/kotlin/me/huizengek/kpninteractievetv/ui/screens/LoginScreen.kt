package me.huizengek.kpninteractievetv.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch
import me.huizengek.kpninteractievetv.Database
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.ui.components.LocalSnackBarHost
import me.huizengek.kpninteractievetv.destinations.AddAccountScreenDestination
import me.huizengek.kpninteractievetv.innertube.Innertube
import me.huizengek.kpninteractievetv.models.Session

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun LoginScreen() {
    val navigator = LocalNavigator.current
    val toaster = LocalSnackBarHost.current

    val sessions by Database.sessions().collectAsState(initial = listOf())

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(sessions) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Innertube.isLoggedIn) {
        if (Innertube.isLoggedIn) navigator.navigateUp()
    }

    BackHandler(enabled = !Innertube.isLoggedIn) {}

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welkom ${if (sessions.isEmpty()) "" else "terug "}bij KPN interactieve TV",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.focusRequester(focusRequester),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sessions) {
                    AccountItem(
                        onClick = {
                            coroutineScope.launch {
                                if (!Innertube.use(it))
                                    toaster.show(
                                        "Er is een fout opgetreden bij het hervatten van uw sessie\n" +
                                                "Probeer het later nog eens"
                                    )
                            }
                        }, session = it
                    )
                }
                item {
                    AccountItem(
                        onClick = {
                            navigator.navigate(AddAccountScreenDestination)
                        },
                        session = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AccountItem(
    session: Session?,
    onClick: () -> Unit
) {
    val focusColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Surface(
            onClick = onClick,
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
}