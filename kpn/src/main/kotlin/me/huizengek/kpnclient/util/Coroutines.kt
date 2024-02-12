package me.huizengek.kpnclient.util

import kotlinx.coroutines.CancellationException

inline fun <reified T> runCatchingCancellable(block: () -> T) =
    runCatching(block).let { if (it.exceptionOrNull() is CancellationException) null else it }
