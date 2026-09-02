package nz.co.warehouseandroidtest.data.repository

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_RESPONSE_JSON
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.local.InMemoryPreferencesDataStore
import nz.co.warehouseandroidtest.data.local.LoginLocalDataSource
import nz.co.warehouseandroidtest.data.loginResponseHeaders
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.createWarehouseHttpClient
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginRepositoryTest {

    @Test
    fun login_storesResultAndTokenInLocalDataSource() = runTest {
        val localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedHttpClient()),
            localDataSource = localDataSource,
        )

        val session = repository.login()

        assertEquals(LOGIN_TOKEN, session.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, session.expiresDatetime)
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
                mockUnauthenticatedHttpClient { requestCount += 1 },
            ),
            localDataSource = localDataSource,
        )

        val session = repository.ensureSession()

        assertEquals("cached-token", session.token)
        assertEquals(0, requestCount)
    }

    @Test
    fun ensureSession_refreshesWhenCachedSessionIsExpired() = runTest {
        val localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        localDataSource.save(sampleSession(expiresDatetime = "2020-01-01T00:00:00Z"))
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedHttpClient()),
            localDataSource = localDataSource,
        )

        val session = repository.ensureSession()

        assertEquals(LOGIN_TOKEN, session.token)
        assertEquals(session, localDataSource.get())
    }

    @Test
    fun ensureSession_logsInWhenLocalDataSourceIsEmpty() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = LOGIN_RESPONSE_JSON.trimIndent(),
                status = HttpStatusCode.OK,
                headers = loginResponseHeaders(),
            )
        }
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                createWarehouseHttpClient(
                    subscriptionKey = "test-subscription-key",
                    device = "iOS",
                    engine = engine,
                ),
            ),
            localDataSource = LoginLocalDataSource(InMemoryPreferencesDataStore()),
        )

        val session = repository.ensureSession()

        assertEquals(LOGIN_TOKEN, session.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, session.expiresDatetime)
    }

    private fun sampleSession(expiresDatetime: String) = LoginSession(
        token = "cached-token",
        expiresDatetime = expiresDatetime,
    )
}
