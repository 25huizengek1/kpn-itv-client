package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.ui.components.TabHost
import me.huizengek.kpninteractievetv.ui.components.tabs
import me.huizengek.kpninteractievetv.ui.screens.destinations.LoginScreenDestination

val tabs = tabs {
    tab(name = "Kijk") {
        NowOnTVScreen()
    }

    tab(name = "Instellingen") {
        SettingsScreen()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen() {
    val navigator = LocalNavigator.current
    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    val isLoggedIn by KpnClient.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) navigator.navigate(LoginScreenDestination)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        val focusRequester = remember { FocusRequester() }
        var wasFocused by rememberSaveable { mutableStateOf(true) }
        var lastFocused: FocusRequester? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        TabRow(
            selectedTabIndex = currentTab,
            modifier = Modifier
                .focusRequester(focusRequester)
                .align(Alignment.CenterHorizontally)
                .onFocusChanged {
                    val isFocusedNow = it.isFocused || it.hasFocus
                    if (!wasFocused && isFocusedNow) lastFocused?.requestFocus()
                    wasFocused = isFocusedNow
                }
        ) {
            tabs.forEachIndexed { index, tab ->
                val interactionSource = remember { MutableInteractionSource() }

                Tab(
                    selected = currentTab == index,
                    onFocus = {
                        currentTab = index
                        lastFocused = tab.focusRequester
                    },
                    modifier = Modifier
                        .indication(
                            interactionSource = interactionSource,
                            indication = null
                        )
                        .focusRequester(tab.focusRequester)
                        .focusProperties { canFocus = currentTab == index || wasFocused },
                    interactionSource = interactionSource
                ) {
                    Text(
                        text = tab.name,
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabHost(
            index = currentTab,
            tabs = tabs,
            modifier = Modifier.fillMaxSize()
        )
    }
}
