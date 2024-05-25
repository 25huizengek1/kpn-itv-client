package me.huizengek.kpninteractievetv.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpninteractievetv.Database
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.preferences.KpnPreferences
import me.huizengek.kpninteractievetv.ui.components.LocalSnackBarHost
import me.huizengek.kpninteractievetv.ui.items.AccountItem
import me.huizengek.kpninteractievetv.ui.screens.destinations.AddAccountScreenDestination
import me.huizengek.kpninteractievetv.util.bold

@Destination
@Composable
fun LoginScreen() {
    val navigator = LocalNavigator.current
    val snackBar = LocalSnackBarHost.current
    val context = LocalContext.current

    val sessions by Database.sessions().collectAsState(initial = listOf())
    val isLoggedIn by KpnClient.isLoggedIn.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(sessions) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) navigator.navigateUp()
    }

    BackHandler(enabled = !isLoggedIn) {}

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (sessions.isEmpty()) stringResource(R.string.welcome)
                else stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineLarge.bold
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
                                if (
                                    KpnClient.use(
                                        session = it.toApiSession(),
                                        deviceId = KpnPreferences.deviceId
                                    )
                                ) return@launch

                                snackBar.show(
                                    context.getString(R.string.error_resume_session)
                                )
                            }
                        },
                        session = it
                    )
                }
                item {
                    AccountItem(
                        onClick = { navigator.navigate(AddAccountScreenDestination) },
                        session = null
                    )
                }
            }
        }
    }
}
