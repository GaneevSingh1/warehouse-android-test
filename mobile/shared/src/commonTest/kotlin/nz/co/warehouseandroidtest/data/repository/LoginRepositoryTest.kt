package nz.co.warehouseandroidtest.data.repository

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.local.AuthLocalDataSource
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginRepositoryTest {

    @Test
    fun getToken_returnsLocalTokenWithoutCallingRemote() = runTest {
        val localDataSource = AuthLocalDataSource()
        localDataSource.save(
            LoginSession(token = "cached-token", expiresDatetime = LOGIN_TOKEN_EXPIRES),
        )
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
            ),
            localDataSource = localDataSource,
        )

        assertEquals("cached-token", repository.getToken().getOrThrow())
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
        assertEquals(LOGIN_TOKEN, localDataSource.get()?.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, localDataSource.get()?.expiresDatetime)
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
        assertNull(localDataSource.get())
    }
}
