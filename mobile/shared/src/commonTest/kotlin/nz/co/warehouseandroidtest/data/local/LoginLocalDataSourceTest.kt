package nz.co.warehouseandroidtest.data.local

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginLocalDataSourceTest {

    @Test
    fun save_thenGet_returnsPersistedSession() = runTest {
        val dataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        val session = sampleSession()

        dataSource.save(session)

        assertEquals(session, dataSource.get())
    }

    @Test
    fun get_returnsNullWhenEmpty() = runTest {
        val dataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())

        assertNull(dataSource.get())
    }

    @Test
    fun clear_removesPersistedSession() = runTest {
        val dataSource = LoginLocalDataSource(InMemoryPreferencesDataStore())
        dataSource.save(sampleSession())

        dataSource.clear()

        assertNull(dataSource.get())
    }

    private fun sampleSession() = LoginSession(
        customerId = "bcbPyICa4tvd4ifoHDRI6IU31B",
        preferredBranchIds = emptyList(),
        eReceiptsPreferred = false,
        isTeamMember = false,
        isStaff = false,
        masterEmailOptIn = false,
        expiresDatetime = "2099-09-01T22:29:04Z",
        expiryMinutes = 29,
        guest = true,
        platformDemandWare = "QAT",
        environment = "Azure QAT",
        developmentPlatform = true,
        apiVersion = 4.9,
        requestedApiVersion = 4.6,
    )
}
