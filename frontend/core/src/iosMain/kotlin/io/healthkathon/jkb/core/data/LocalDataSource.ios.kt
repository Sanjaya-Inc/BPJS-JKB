package io.healthkathon.jkb.core.data

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual class LocalDataSource : Settings by NSUserDefaultsSettings(NSUserDefaults())

actual fun createLocalDataSource(): LocalDataSource {
    return LocalDataSource()
}
