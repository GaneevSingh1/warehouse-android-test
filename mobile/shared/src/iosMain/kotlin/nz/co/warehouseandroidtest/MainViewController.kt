package nz.co.warehouseandroidtest

import androidx.compose.ui.window.ComposeUIViewController
import nz.co.warehouseandroidtest.di.appModules
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = run {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(appModules)
        }
    }
    ComposeUIViewController { App() }
}
