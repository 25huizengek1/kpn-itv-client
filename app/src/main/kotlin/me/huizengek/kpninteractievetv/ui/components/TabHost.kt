package me.huizengek.kpninteractievetv.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

data class Tab(
    val idx: Int,
    val name: String,
    val content: @Composable () -> Unit,
    val focusRequester: FocusRequester = FocusRequester()
)

class TabBuilder {
    private val tabs = mutableSetOf<Tab>()

    fun tab(
        name: String,
        content: @Composable () -> Unit
    ) {
        tabs += Tab(
            idx = tabs.size,
            name = name,
            content = content
        )
    }

    fun build() = tabs.toList().sortedBy { it.idx }
}

fun tabs(block: TabBuilder.() -> Unit) = TabBuilder().apply(block).build()

@Composable
fun TabHost(
    index: Int,
    tabs: List<Tab>,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = index,
        transitionSpec = {
            val direction =
                if (targetState > initialState) AnimatedContentTransitionScope.SlideDirection.Left
                else AnimatedContentTransitionScope.SlideDirection.Right

            ContentTransform(
                targetContentEnter = slideIntoContainer(
                    towards = direction,
                    animationSpec = tween(500)
                ),
                initialContentExit = slideOutOfContainer(
                    towards = direction,
                    animationSpec = tween(500)
                ),
                sizeTransform = null
            )
        },
        label = "",
        modifier = modifier
    ) { i ->
        tabs[i].content()
    }
}
