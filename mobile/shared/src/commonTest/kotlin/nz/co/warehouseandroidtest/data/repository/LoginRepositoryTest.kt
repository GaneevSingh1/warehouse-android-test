package nz.co.warehouseandroidtest.data.repository

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.local.InMemoryPreferencesDataStore
import nz.co.warehouseandroidtest.data.local.LoginLocalDataSource
import nz.co.warehouseandroidtest.data.mockUnauthenticatedApiClient
import nz.co.warehouseandroidtest.data.remote.UnauthenticatedApiClient
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginRepositoryTest {

    @Test
    fun login_storesResultInLocalDataSource() = runTest {
        val localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedApiClient()),
            localDataSource = localDataSource,
        )

        val session = repository.login()

        assertEquals("bcbPyICa4tvd4ifoHDRI6IU31B", session.customerId)
        assertEquals(session, localDataSource.get())
        assertEquals(session, repository.getCachedSession())
    }

    @Test
    fun ensureSession_usesCachedSessionWhenStillValid() = runTest {
        var requestCount = 0
        val localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        localDataSource.save(sampleSession(expiresDatetime = "2099-09-01T22:29:04Z"))
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedApiClient { requestCount += 1 },
            ),
            localDataSource = localDataSource,
        )

        val session = repository.ensureSession()

        assertEquals("cached-customer", session.customerId)
        assertEquals(0, requestCount)
    }

    @Test
    fun ensureSession_refreshesWhenCachedSessionIsExpired() = runTest {
        val localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        localDataSource.save(sampleSession(expiresDatetime = "2020-01-01T00:00:00Z"))
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedApiClient()),
            localDataSource = localDataSource,
        )

        val session = repository.ensureSession()

        assertEquals("bcbPyICa4tvd4ifoHDRI6IU31B", session.customerId)
        assertEquals(session, localDataSource.get())
    }

    @Test
    fun ensureSession_logsInWhenLocalDataSourceIsEmpty() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = LOGIN_RESPONSE_JSON.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                UnauthenticatedApiClient.create(
                    subscriptionKey = "test-subscription-key",
                    device = "iOS",
                    engine = engine,
                ),
            ),
            localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore()),
        )

        val session = repository.ensureSession()

        assertEquals("bcbPyICa4tvd4ifoHDRI6IU31B", session.customerId)
        assertEquals(true, session.guest)
    }

    private fun sampleSession(expiresDatetime: String) = LoginSession(
        customerId = "cached-customer",
        preferredBranchIds = emptyList(),
        eReceiptsPreferred = false,
        isTeamMember = false,
        isStaff = false,
        masterEmailOptIn = false,
        expiresDatetime = expiresDatetime,
        expiryMinutes = 29,
        guest = true,
        platformDemandWare = "QAT",
        environment = "Azure QAT",
        developmentPlatform = true,
        apiVersion = 4.9,
        requestedApiVersion = 4.6,
    )
}
