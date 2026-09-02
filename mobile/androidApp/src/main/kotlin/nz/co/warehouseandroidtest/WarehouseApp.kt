package nz.co.warehouseandroidtest

import android.app.Application
import nz.co.warehouseandroidtest.di.sharedModule
import org.koin.core.context.startKoin

class WarehouseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(sharedModule)
        }
    }
}
