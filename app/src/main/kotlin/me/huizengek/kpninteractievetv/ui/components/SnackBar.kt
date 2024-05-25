package me.huizengek.kpninteractievetv.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.huizengek.kpninteractievetv.util.lateinitCompositionLocalOf

class SnackBarHost {
    private val mutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    var state: String? by mutableStateOf(null)
        private set

    fun show(
        text: String,
        delay: Long = 3000
    ) = coroutineScope.launch {
        mutex.withLock {
            state = text
            delay(delay)
            state = null
        }
    }
}

@Composable
fun SnackBarHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Box(modifier = modifier) {
    val snackBarHost = remember { SnackBarHost() }

    CompositionLocalProvider(
        value = LocalSnackBarHost provides snackBarHost,
        content = content
    )

    AnimatedContent(
        targetState = snackBarHost.state,
        label = "",
        transitionSpec = {
            ContentTransform(
                targetContentEnter = fadeIn(),
                initialContentExit = fadeOut(),
                sizeTransform = null
            )
        }
    ) { text ->
        if (text != null) Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(all = 24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(all = 16.dp)
            )
        }
    }
}

val LocalSnackBarHost = lateinitCompositionLocalOf<SnackBarHost>()
