package io.healthkathon.jkb.core.data

import android.content.Context.MODE_PRIVATE
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.healthkathon.jkb.core.presentation.utils.PlatformContext
import org.koin.java.KoinJavaComponent
import kotlin.jvm.java

actual class LocalDataSource(
    private val context: PlatformContext
) : Settings by SharedPreferencesSettings(
    context.appContext.getSharedPreferences(
        "jkb-pref",
        MODE_PRIVATE
    )
)

actual fun createLocalDataSource(): LocalDataSource {
    val context = KoinJavaComponent.get<PlatformContext>(
        PlatformContext::class.java
    )
    return LocalDataSource(context)
}
