package io.healthkathon.jkb.frauddetection.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.healthkathon.jkb.core.data.remote.RemoteApiConfigProvider
import io.healthkathon.jkb.frauddetection.data.FraudDetectionMockedApi
import io.healthkathon.jkb.frauddetection.data.FraudDetectionRemoteApi
import io.healthkathon.jkb.frauddetection.data.createFraudDetectionRemoteApi
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("io.healthkathon.jkb.frauddetection")
object FraudDetectionModule {

    @Single
    fun provideFraudDetectionRemoteApi(
        ktorfit: Ktorfit,
        config: RemoteApiConfigProvider
    ): FraudDetectionRemoteApi {
        return if (config.isMock()) {
            FraudDetectionMockedApi()
        } else {
            ktorfit.createFraudDetectionRemoteApi()
        }
    }
}
