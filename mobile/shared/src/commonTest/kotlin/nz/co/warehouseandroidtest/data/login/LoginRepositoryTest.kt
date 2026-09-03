package nz.co.warehouseandroidtest.data.login

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.EXPIRED_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient
import nz.co.warehouseandroidtest.domain.login.LoginSession

class LoginRepositoryTest {

    @Test
    fun getToken_returnsLocalTokenWithoutCallingRemote() = runTest {
        val localDataSource = AuthLocalDataSource()
        localDataSource.saveLoginSession(
            LoginSession(token = "cached-token", expiresDatetime = LOGIN_TOKEN_EXPIRES),
        )
        var loginCalls = 0
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient(status = HttpStatusCode.Unauthorized) {
                    loginCalls++
                },
            ),
            localDataSource = localDataSource,
        )

        assertEquals("cached-token", repository.getToken().getOrThrow())
        assertEquals(0, loginCalls)
    }

    @Test
    fun getToken_fetchesFromRemoteWhenLocalIsEmpty() = runTest {
        val localDataSource = AuthLocalDataSource()
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedHttpClient()),
            localDataSource = localDataSource,
        )

        val token = repository.getToken().getOrThrow()

        assertEquals(LOGIN_TOKEN, token)
        assertEquals(LOGIN_TOKEN, localDataSource.getLoginSession()?.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, localDataSource.getLoginSession()?.expiresDatetime)
    }

    @Test
    fun getToken_refreshesWhenCachedTokenIsExpired() = runTest {
        val localDataSource = AuthLocalDataSource()
        localDataSource.saveLoginSession(
            LoginSession(token = "expired-token", expiresDatetime = EXPIRED_TOKEN_EXPIRES),
        )
        var loginCalls = 0
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient { loginCalls++ },
            ),
            localDataSource = localDataSource,
        )

        assertEquals(LOGIN_TOKEN, repository.getToken().getOrThrow())
        assertEquals(1, loginCalls)
        assertEquals(LOGIN_TOKEN, localDataSource.getLoginSession()?.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, localDataSource.getLoginSession()?.expiresDatetime)
    }

    @Test
    fun getToken_clearsExpiredSessionWhenRefreshFails() = runTest {
        val localDataSource = AuthLocalDataSource()
        localDataSource.saveLoginSession(
            LoginSession(token = "expired-token", expiresDatetime = EXPIRED_TOKEN_EXPIRES),
        )
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
            ),
            localDataSource = localDataSource,
        )

        val result = repository.getToken()

        assertTrue(result.isFailure)
        assertNull(localDataSource.getLoginSession())
    }

    @Test
    fun getToken_doesNotStoreSessionWhenRemoteFails() = runTest {
        val localDataSource = AuthLocalDataSource()
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
            ),
            localDataSource = localDataSource,
        )

        val result = repository.getToken()

        assertTrue(result.isFailure)
        assertNull(localDataSource.getLoginSession())
    }

    @Test
    fun getToken_doesNotStoreExpiredLoginResponse() = runTest {
        val localDataSource = AuthLocalDataSource()
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient(expiresDatetime = EXPIRED_TOKEN_EXPIRES),
            ),
            localDataSource = localDataSource,
        )

        val result = repository.getToken()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message.orEmpty().contains("expired"))
        assertNull(localDataSource.getLoginSession())
    }

    @Test
    fun invalidateSession_forcesNextGetTokenToLogin() = runTest {
        val localDataSource = AuthLocalDataSource()
        localDataSource.saveLoginSession(
            LoginSession(token = "cached-token", expiresDatetime = LOGIN_TOKEN_EXPIRES),
        )
        var loginCalls = 0
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient { loginCalls++ },
            ),
            localDataSource = localDataSource,
        )

        repository.invalidateSession()

        assertEquals(LOGIN_TOKEN, repository.getToken().getOrThrow())
        assertEquals(1, loginCalls)
    }
}
