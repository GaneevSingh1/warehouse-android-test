package nz.co.warehouseandroidtest

import androidx.compose.ui.window.ComposeUIViewController
import nz.co.warehouseandroidtest.di.initKoin

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}
