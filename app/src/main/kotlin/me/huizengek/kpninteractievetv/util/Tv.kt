package me.huizengek.kpninteractievetv.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

// Adapted from https://issuetracker.google.com/issues/348896032
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyLayoutPivotProvider(
    parentFraction: Float = 0.2f,
    childFraction: Float = 0f,
    content: @Composable () -> Unit,
) {
    val bringIntoViewSpec = remember(parentFraction, childFraction) {
        object : BringIntoViewSpec {
            override fun calculateScrollDistance(
                offset: Float,
                size: Float,
                containerSize: Float
            ): Float {
                val leadingEdge = parentFraction * containerSize - (childFraction * size)
                val availableSpace = containerSize - leadingEdge

                return offset - if (size <= containerSize && availableSpace < size)
                    containerSize - size else leadingEdge
            }
        }
    }

    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}
