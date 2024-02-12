package me.huizengek.kpninteractievetv.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.huizengek.kpnclient.SessionInfo

@Entity(indices = [Index(value = ["username"], unique = true)])
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String,
    val displayName: String? = null
) {
    fun toApiSession() = SessionInfo(
        username = username,
        password = password,
        displayName = displayName
    )
}

fun SessionInfo.toDbSession() = Session(
    username = username,
    password = password,
    displayName = displayName
)
