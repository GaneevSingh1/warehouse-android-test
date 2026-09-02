package nz.co.warehouseandroidtest.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import nz.co.warehouseandroidtest.data.local.LOGIN_DATA_STORE_FILE_NAME
import nz.co.warehouseandroidtest.data.local.createPreferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DataStore<Preferences>> {
        val context = androidContext()
        createPreferencesDataStore {
            context.filesDir.resolve(LOGIN_DATA_STORE_FILE_NAME).absolutePath
        }
    }
}
