package nz.co.warehouseandroidtest

import coil3.ImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory

internal actual fun ImageLoader.Builder.addPlatformNetworkFetcher(): ImageLoader.Builder =
    components {
        add(KtorNetworkFetcherFactory())
    }
