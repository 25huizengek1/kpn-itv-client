package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.ui.components.TabHost
import me.huizengek.kpninteractievetv.ui.components.TabRow
import me.huizengek.kpninteractievetv.ui.components.tabs
import me.huizengek.kpninteractievetv.ui.screens.destinations.LoginScreenDestination

val tabs = tabs {
    tab(name = { stringResource(R.string.watch) }) {
        NowOnTVScreen()
    }

    tab(name = { stringResource(R.string.settings) }) {
        SettingsScreen()
    }
}

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
        TabRow(
            tabs = tabs,
            currentTab = currentTab,
            setCurrentTab = { currentTab = it },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TabHost(
            tabs = tabs,
            index = currentTab,
            modifier = Modifier.fillMaxSize()
        )
    }
}
