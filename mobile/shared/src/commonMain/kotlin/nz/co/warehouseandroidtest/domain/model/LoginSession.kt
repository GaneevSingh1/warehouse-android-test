package nz.co.warehouseandroidtest.domain.model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LoginSession(
    val customerId: String,
    val preferredBranchIds: List<Int>,
    val eReceiptsPreferred: Boolean,
    val isTeamMember: Boolean,
    val isStaff: Boolean,
    val masterEmailOptIn: Boolean,
    val expiresDatetime: String,
    val expiryMinutes: Int,
    val guest: Boolean,
    val platformDemandWare: String,
    val environment: String,
    val developmentPlatform: Boolean,
    val apiVersion: Double,
    val requestedApiVersion: Double,
)

@OptIn(ExperimentalTime::class)
fun LoginSession.isExpired(now: Instant = Clock.System.now()): Boolean {
    val expiry = runCatching { Instant.parse(expiresDatetime) }.getOrNull() ?: return true
    return now >= expiry
}
