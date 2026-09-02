package nz.co.warehouseandroidtest.data.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN
import nz.co.warehouseandroidtest.data.LOGIN_TOKEN_EXPIRES
import nz.co.warehouseandroidtest.data.local.InMemoryPreferencesDataStore
import nz.co.warehouseandroidtest.data.local.LoginLocalDataSource
import nz.co.warehouseandroidtest.data.mockUnauthenticatedHttpClient
import nz.co.warehouseandroidtest.data.remote.login.LoginRemoteDataSource

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
}
