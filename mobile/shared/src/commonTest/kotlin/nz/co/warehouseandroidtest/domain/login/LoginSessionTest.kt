package nz.co.warehouseandroidtest.domain.login

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class LoginSessionTest {

    @Test
    fun isExpired_isFalseBeforeExpiry() {
        val session = session(expiresDatetime = "2099-09-01T22:29:04Z")

        assertFalse(session.isExpired(now = Instant.parse("2026-09-01T22:29:04Z")))
    }

    @Test
    fun isExpired_isTrueAtExpiry() {
        val session = session(expiresDatetime = "2026-09-01T22:29:04Z")

        assertTrue(session.isExpired(now = Instant.parse("2026-09-01T22:29:04Z")))
    }

    private fun session(expiresDatetime: String) = LoginSession(
        token = "test-twl-token",
        expiresDatetime = expiresDatetime,
    )
}
