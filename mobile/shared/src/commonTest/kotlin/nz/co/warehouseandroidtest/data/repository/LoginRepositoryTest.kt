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

class LoginRepositoryTest {

    @Test
    fun login_storesResultAndTokenInLocalDataSource() = runTest {
        val localDataSource = AuthLocalDataSource()
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(mockUnauthenticatedHttpClient()),
            localDataSource = localDataSource,
        )

        val session = repository.login().getOrThrow()

        assertEquals(LOGIN_TOKEN, session.token)
        assertEquals(LOGIN_TOKEN_EXPIRES, session.expiresDatetime)
        assertEquals(session, localDataSource.get())
        assertEquals(session, repository.getCachedSession())
    }

    @Test
    fun login_doesNotStoreSessionWhenRequestFails() = runTest {
        val localDataSource = AuthLocalDataSource()
        val repository = LoginRepository(
            remoteDataSource = LoginRemoteDataSource(
                mockUnauthenticatedHttpClient(status = HttpStatusCode.Unauthorized),
            ),
            localDataSource = localDataSource,
        )

        val result = repository.login()

        assertTrue(result.isFailure)
        assertNull(localDataSource.get())
    }
}
