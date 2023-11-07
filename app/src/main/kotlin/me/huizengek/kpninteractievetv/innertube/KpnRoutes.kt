package me.huizengek.kpninteractievetv.innertube

internal const val root = "https://api.tv.prod.itvavs.prod.aws.kpn.com/101/1.2.0/A/nld/pctv/kpn"

internal const val sessions = "$root/USER/SESSIONS"
internal const val liveChannels = "$root/TRAY/LIVECHANNELS"
internal const val dshToken = "$root/USER/DSHTOKEN"

internal fun channels(channelId: Any, assetId: Any) = "$root/CONTENT/VIDEOURL/LIVE/$channelId/$assetId"