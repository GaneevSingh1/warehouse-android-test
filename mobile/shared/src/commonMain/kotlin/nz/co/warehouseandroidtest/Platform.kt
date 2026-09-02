package nz.co.warehouseandroidtest

interface Platform {
    val name: String
    val twlDeviceHeader: String
}

expect fun getPlatform(): Platform
