package me.huizengek.kpninteractievetv.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import me.huizengek.kpninteractievetv.BuildConfig
import me.huizengek.kpninteractievetv.Dependencies
import me.huizengek.kpninteractievetv.R

const val NOTIFICATION_ID = 1001
const val DEFAULT_CHANNEL_ID = "default_channel_id"

class PlaybackService : MediaSessionService(), Player.Listener {
    private var session: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = session

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_NONE
        )
        getSystemService<NotificationManager>()?.createNotificationChannel(channel)

        startForeground(
            NOTIFICATION_ID,
            NotificationCompat.Builder(
                applicationContext,
                DEFAULT_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )
        return super.onStartCommand(intent, flags, startId)
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(applicationContext).apply {
                    setLiveTargetOffsetMs(5000)
                }
            ).setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                if (BuildConfig.DEBUG) addAnalyticsListener(EventLogger())
                addListener(this@PlaybackService)
            }

        Dependencies.player = player
        session = MediaSession.Builder(this, player).build()
    }

    override fun onPlayerError(error: PlaybackException) {
        if (error.errorCode != PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW)
            return error.printStackTrace()

        session?.player?.seekToDefaultPosition()
        session?.player?.prepare()
    }

    override fun onDestroy() {
        session?.run {
            player.release()
            release()
            session = null
        }
        super.onDestroy()
    }
}
