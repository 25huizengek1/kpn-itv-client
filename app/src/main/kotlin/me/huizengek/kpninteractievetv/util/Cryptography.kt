package me.huizengek.kpninteractievetv.util

import java.security.MessageDigest

val ByteArray.sha256
    get() = MessageDigest.getInstance("SHA-256").digest(this)
        .fold("") { acc, byte -> acc + byte.toString(16) }