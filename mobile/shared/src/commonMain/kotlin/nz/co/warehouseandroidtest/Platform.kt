package nz.co.warehouseandroidtest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
