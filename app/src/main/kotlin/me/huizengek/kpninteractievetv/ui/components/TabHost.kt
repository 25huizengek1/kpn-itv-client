package me.huizengek.kpninteractievetv.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Tab
import androidx.tv.material3.Text
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class Tab(
    val idx: Int,
    val name: @Composable () -> String,
    val content: @Composable () -> Unit,
    val focusRequester: FocusRequester = FocusRequester()
)

class TabBuilder {
    private val tabs = mutableSetOf<Tab>()

    fun tab(
        name: @Composable () -> String,
        content: @Composable () -> Unit
    ) {
        tabs += Tab(
            idx = tabs.size,
            name = name,
            content = content
        )
    }

    fun build() = tabs.toList().sortedBy { it.idx }.toImmutableList()
}

fun tabs(block: TabBuilder.() -> Unit) = TabBuilder().apply(block).build()

@Composable
fun TabHost(
    index: Int,
    tabs: List<Tab>,
    modifier: Modifier = Modifier
) = AnimatedContent(
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TabRow(
    tabs: ImmutableList<Tab>,
    currentTab: Int,
    setCurrentTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var wasFocused by rememberSaveable { mutableStateOf(true) }
    var lastFocused: FocusRequester? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    androidx.tv.material3.TabRow(
        selectedTabIndex = currentTab,
        modifier = modifier
            .focusRequester(focusRequester)
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
                    setCurrentTab(index)
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
                    text = tab.name(),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}
