package nz.co.warehouseandroidtest.data.login

import nz.co.warehouseandroidtest.domain.login.LoginSession

class AuthLocalDataSource {
    private var session: LoginSession? = null

    fun saveLoginSession(session: LoginSession) {
        this.session = session
    }

    fun getLoginSession(): LoginSession? = session

    fun clearLoginSession() {
        session = null
    }
}
