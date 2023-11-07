package me.huizengek.kpninteractievetv.util

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

operator fun JsonElement.div(other: String) = jsonObject[other]!!