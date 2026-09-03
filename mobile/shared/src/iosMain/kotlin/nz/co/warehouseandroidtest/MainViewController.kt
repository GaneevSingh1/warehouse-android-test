package nz.co.warehouseandroidtest

import androidx.compose.ui.window.ComposeUIViewController
import nz.co.warehouseandroidtest.di.appModules
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = run {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin {
            modules(appModules)
        }
    }
    ComposeUIViewController { App() }
}
