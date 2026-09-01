package nz.co.warehouseandroidtest

class Greeting {
    private val platform = getPlatform()

    fun greet(): String = sayHello(platform.name)
}
