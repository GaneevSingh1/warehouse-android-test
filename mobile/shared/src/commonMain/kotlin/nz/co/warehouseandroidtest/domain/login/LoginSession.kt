package nz.co.warehouseandroidtest.domain.login

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class LoginSession(
    val token: String,
    val expiresDatetime: String,
)

@OptIn(ExperimentalTime::class)
fun LoginSession.isExpired(now: Instant = Clock.System.now()): Boolean {
    if (token.isBlank()) return true
    val expiry = runCatching { Instant.parse(expiresDatetime) }.getOrNull() ?: return true
    return now >= expiry
}
