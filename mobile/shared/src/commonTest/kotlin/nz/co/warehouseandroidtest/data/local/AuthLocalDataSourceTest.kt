package nz.co.warehouseandroidtest.data.local

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.domain.model.LoginSession

class AuthLocalDataSourceTest {

    @Test
    fun save_thenGet_returnsPersistedSession() = runTest {
        val dataSource = AuthLocalDataSource()
        val session = sampleSession()

        dataSource.save(session)

        assertEquals(session, dataSource.get())
    }

    @Test
    fun get_returnsNullWhenEmpty() = runTest {
        val dataSource = AuthLocalDataSource()

        assertNull(dataSource.get())
    }

    @Test
    fun clear_removesPersistedSession() = runTest {
        val dataSource = AuthLocalDataSource()
        dataSource.save(sampleSession())

        dataSource.clear()

        assertNull(dataSource.get())
    }

    private fun sampleSession() = LoginSession(
        token = "test-twl-token",
        expiresDatetime = "2099-09-01T22:29:04Z",
    )
}
