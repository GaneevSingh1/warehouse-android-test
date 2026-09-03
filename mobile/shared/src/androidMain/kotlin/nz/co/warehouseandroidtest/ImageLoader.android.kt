package nz.co.warehouseandroidtest

import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory

internal actual fun ImageLoader.Builder.addPlatformNetworkFetcher(): ImageLoader.Builder =
    components {
        add(OkHttpNetworkFetcherFactory())
    }
