package me.huizengek.kpnclient

import io.ktor.client.request.header
import io.ktor.http.HttpMessageBuilder

internal fun HttpMessageBuilder.kpnHeaders() {
    header("authority", "api.tv.prod.itvavs.prod.aws.kpn.com")
    header("accept-language", "nl-NL,nl;q=0.9")
    header("avssite", "http://www.itvonline.nl")
    header("cache-control", "no-cache")
    header("origin", "https://tv.kpn.com")
    header("pragma", "no-cache")
    header("referer", "https://tv.kpn.com/")
    header(
        "sec-ch-ua",
        "\"Chromium\";v=\"112\", \"Google Chrome\";v=\"112\", \"Not:A-Brand\";v=\"99\""
    )
    header("sec-ch-ua-mobile", "?0")
    header("sec-ch-ua-platform", "\"Windows\"")
    header("sec-fetch-dest", "empty")
    header("sec-fetch-mode", "cors")
    header("sec-fetch-site", "same-site")
    header(
        "user-agent",
        @Suppress("MaximumLineLength")
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"
    )
    header("x-xsrf-token", "null")
}

internal const val PROFILE = "G03"
