package me.huizengek.kpninteractievetv

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.google.common.util.concurrent.ListenableFuture
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.isRouteOnBackStackAsState
import kotlinx.coroutines.flow.MutableStateFlow
import me.huizengek.kpnclient.ChannelContainer
import me.huizengek.kpninteractievetv.preferences.PreferencesHolder
import me.huizengek.kpninteractievetv.service.PlaybackService
import me.huizengek.kpninteractievetv.ui.components.SnackBarHost
import me.huizengek.kpninteractievetv.ui.screens.NavGraphs
import me.huizengek.kpninteractievetv.ui.screens.destinations.WatchScreenDestination
import me.huizengek.kpninteractievetv.ui.theme.MaterialContext
import me.huizengek.kpninteractievetv.util.component
import me.huizengek.kpninteractievetv.util.lateinitCompositionLocalOf

object Dependencies {
    lateinit var application: InteractieveTVApplication
    var player: Player? by mutableStateOf(null)

    context(InteractieveTVApplication)
    internal fun init() {
        application = this@InteractieveTVApplication
        DatabaseAccessor.init()
    }
}

open class GlobalPreferencesHolder : PreferencesHolder(Dependencies.application, "preferences")

class InteractieveTVApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        Dependencies.init()
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .crossfade(true)
        .respectCacheHeaders(false)
        .diskCache(
            DiskCache.Builder()
                .directory(cacheDir.resolve("coil"))
                .maxSizeBytes(104857600L)
                .build()
        ).build()
}

val channelState = MutableStateFlow<ChannelContainer?>(null)

class MainActivity : ComponentActivity() {
    private lateinit var mediaController: ListenableFuture<MediaController>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionToken = SessionToken(applicationContext, component<PlaybackService>())

        mediaController = MediaController.Builder(applicationContext, sessionToken).buildAsync()

        startForegroundService(Intent(applicationContext, PlaybackService::class.java))

        setContent {
            val engine = rememberNavHostEngine()
            val controller = engine.rememberNavController()

            MaterialContext {
                SnackBarHost(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(
                        LocalNavigator provides controller,
                        LocalPlayer provides Dependencies.player
                    ) {
                        val channel by channelState.collectAsState()
                        val isWatching by controller.isRouteOnBackStackAsState(route = WatchScreenDestination)

                        LaunchedEffect(channel) {
                            if (channel != null && !isWatching)
                                controller.navigate(WatchScreenDestination)
                        }

                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            modifier = Modifier.fillMaxSize(),
                            engine = engine,
                            navController = controller
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        MediaController.releaseFuture(mediaController)
    }
}

val LocalNavigator = lateinitCompositionLocalOf<NavController>()
val LocalPlayer = compositionLocalOf<Player?> { null }
