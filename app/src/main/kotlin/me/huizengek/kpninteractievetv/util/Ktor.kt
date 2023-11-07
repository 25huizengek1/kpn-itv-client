package me.huizengek.kpninteractievetv.util

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.renderCookieHeader

fun HttpMessageBuilder.cookie(cookie: Cookie) {
    val rendered = renderCookieHeader(cookie)
    headers[HttpHeaders.Cookie] = headers[HttpHeaders.Cookie]?.let { "$it; $rendered" } ?: rendered
}

context(HttpRequestBuilder)
operator fun String.plusAssign(other: String) = parameter(this, other)