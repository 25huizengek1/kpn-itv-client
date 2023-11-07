package me.huizengek.kpninteractievetv.innertube

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.setCookie
import me.huizengek.kpninteractievetv.util.cookie

@JvmInline
value class CookieJar(
    private val cookies: MutableList<Cookie> = mutableListOf()
) : MutableList<Cookie> by cookies {
    context(HttpMessageBuilder)
    fun use() = forEach { cookie(it) }
}

fun HttpResponse.save(jar: CookieJar) = apply {
    setCookie().forEach { c ->
        jar.removeIf { it.name == c.name }
        jar += c
    }
}