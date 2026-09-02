package nz.co.warehouseandroidtest

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val twlDeviceHeader: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()
