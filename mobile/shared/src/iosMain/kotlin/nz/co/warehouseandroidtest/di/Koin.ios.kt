package nz.co.warehouseandroidtest.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import nz.co.warehouseandroidtest.data.local.LOGIN_DATA_STORE_FILE_NAME
import nz.co.warehouseandroidtest.data.local.createPreferencesDataStore
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val platformModule = module {
    single<DataStore<Preferences>> {
        createPreferencesDataStore(::loginDataStorePath)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun loginDataStorePath(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path) + "/$LOGIN_DATA_STORE_FILE_NAME"
}
