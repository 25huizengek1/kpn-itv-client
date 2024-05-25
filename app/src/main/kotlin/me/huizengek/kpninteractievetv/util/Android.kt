package me.huizengek.kpninteractievetv.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Suppress("ModifierComposed")
fun Modifier.focusOnLaunch(key: Any = Unit) = composed {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key) {
        focusRequester.requestFocus()
    }

    focusRequester(focusRequester)
}

fun Context.findActivity(): Activity {
    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    error("Should be called in the context of an Activity")
}

@Composable
fun focusRequesters(): LazyMap<FocusRequester> {
    val requesters = remember { mutableStateMapOf<Int, FocusRequester>() }

    return LazyMap {
        requesters.getOrPut(it) { FocusRequester() }
    }
}

fun interface LazyMap<T> {
    operator fun get(index: Int): T
}

inline fun <reified T> Context.component() = ComponentName(applicationContext, T::class.java)
inline fun <reified T> Context.intent() = Intent(applicationContext, T::class.java)
