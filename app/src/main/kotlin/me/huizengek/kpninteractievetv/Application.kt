package me.huizengek.kpninteractievetv

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.rememberNavHostEngine
import me.huizengek.kpninteractievetv.ui.components.SnackBarHost
import me.huizengek.kpninteractievetv.ui.screens.HomeScreen
import me.huizengek.kpninteractievetv.service.PlaybackService
import me.huizengek.kpninteractievetv.ui.theme.MaterialContext
import me.huizengek.kpninteractievetv.util.lateinitCompositionLocalOf

class InteractieveTVApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        DependencyGraph.init()
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

class MainActivity : ComponentActivity() {
    private lateinit var mediaController: ListenableFuture<MediaController>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionToken = SessionToken(
            /* context          = */ applicationContext,
            /* serviceComponent = */ ComponentName(applicationContext, PlaybackService::class.java)
        )

        mediaController = MediaController.Builder(applicationContext, sessionToken).buildAsync()

        startForegroundService(Intent(applicationContext, PlaybackService::class.java))

        setContent {
            val engine = rememberNavHostEngine()
            val controller = engine.rememberNavController()

            MaterialContext {
                SnackBarHost(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(
                        LocalNavigator provides controller,
                        LocalPlayer provides (DependencyGraph.player)
                    ) {
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

@RootNavGraph(start = true)
@Destination
@Composable
fun NavRoot() = HomeScreen()

val LocalNavigator = lateinitCompositionLocalOf<NavController>()
val LocalPlayer = compositionLocalOf<Player?> { null }