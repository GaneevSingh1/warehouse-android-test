package nz.co.warehouseandroidtest.data.remote.login

import kotlinx.serialization.Serializable
import nz.co.warehouseandroidtest.data.remote.UnauthenticatedApiClient
import nz.co.warehouseandroidtest.domain.model.LoginSession

internal const val LOGIN_PATH = "Login.json"

@Serializable
data class LoginResponseDto(
    val customerId: String,
    val preferredBranchIds: List<Int> = emptyList(),
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

fun LoginResponseDto.toSession(): LoginSession = LoginSession(
    customerId = customerId,
    preferredBranchIds = preferredBranchIds,
    eReceiptsPreferred = eReceiptsPreferred,
    isTeamMember = isTeamMember,
    isStaff = isStaff,
    masterEmailOptIn = masterEmailOptIn,
    expiresDatetime = expiresDatetime,
    expiryMinutes = expiryMinutes,
    guest = guest,
    platformDemandWare = platformDemandWare,
    environment = environment,
    developmentPlatform = developmentPlatform,
    apiVersion = apiVersion,
    requestedApiVersion = requestedApiVersion,
)

class LoginRemoteDataSource(
    private val apiClient: UnauthenticatedApiClient,
) {
    suspend fun login(): LoginResponseDto = apiClient.get(LOGIN_PATH)
}
