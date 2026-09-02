package nz.co.warehouseandroidtest

import androidx.compose.ui.window.ComposeUIViewController
import nz.co.warehouseandroidtest.di.sharedModule
import org.koin.core.context.startKoin

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = run {
    startKoin {
        modules(sharedModule)
    }
    ComposeUIViewController { App() }
}
