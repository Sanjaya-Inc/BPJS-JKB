package io.healthkathon.jkb.core.data

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual class LocalDataSource : Settings by PreferencesSettings(
    Preferences.userRoot().node("jkb-pref")
)

actual fun createLocalDataSource(): LocalDataSource {
    // JVM implementation doesn't need Context to be created,
    // so we can instantiate it directly without Koin lookup.
    return LocalDataSource()
}
