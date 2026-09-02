package nz.co.warehouseandroidtest.data.local

import nz.co.warehouseandroidtest.domain.model.LoginSession

class AuthLocalDataSource {
    private var session: LoginSession? = null

    suspend fun save(session: LoginSession) {
        this.session = session
    }

    suspend fun get(): LoginSession? = session

    suspend fun clear() {
        session = null
    }
}
