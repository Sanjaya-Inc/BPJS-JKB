package io.healthkathon.jkb.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual class LocalDataSource : Settings by StorageSettings()

actual fun createLocalDataSource(): LocalDataSource {
    return LocalDataSource()
}
