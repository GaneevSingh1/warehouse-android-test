package nz.co.warehouseandroidtest

import android.app.Application
import nz.co.warehouseandroidtest.di.initKoin

class WarehouseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}
