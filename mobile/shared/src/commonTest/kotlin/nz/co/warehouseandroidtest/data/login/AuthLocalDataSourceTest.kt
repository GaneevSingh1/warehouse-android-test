package nz.co.warehouseandroidtest.data.login

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.domain.login.LoginSession

class AuthLocalDataSourceTest {

    @Test
    fun save_LoginSession_thenGet_LoginSession_returnsPersistedSession() = runTest {
        val dataSource = AuthLocalDataSource()
        val session = sampleSession()

        dataSource.saveLoginSession(session)

        assertEquals(session, dataSource.getLoginSession())
    }

    @Test
    fun get_LoginSession_returnsNullWhenEmpty() = runTest {
        val dataSource = AuthLocalDataSource()

        assertNull(dataSource.getLoginSession())
    }

    @Test
    fun clear_LoginSession_removesPersistedSession() = runTest {
        val dataSource = AuthLocalDataSource()
        dataSource.saveLoginSession(sampleSession())

        dataSource.clearLoginSession()

        assertNull(dataSource.getLoginSession())
    }

    private fun sampleSession() = LoginSession(
        token = "test-twl-token",
        expiresDatetime = "2099-09-01T22:29:04Z",
    )
}
