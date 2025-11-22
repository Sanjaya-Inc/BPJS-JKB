package io.healthkathon.jkb.core.domain

import de.jensklingenberg.ktorfit.Ktorfit
import io.healthkathon.jkb.core.data.model.ServiceVersionData
import io.healthkathon.jkb.core.data.remote.BaseUrlProvider
import io.healthkathon.jkb.core.data.remote.CoreRemoteApi
import io.healthkathon.jkb.core.data.remote.createCoreRemoteApi
import org.koin.core.annotation.Single

@Single
class CoreRepository(
    private val ktorfit: Ktorfit,
    private val baseUrlProvider: BaseUrlProvider,
    private val coreRemoteApi: CoreRemoteApi = ktorfit.createCoreRemoteApi()
) {
    suspend fun getServiceData(): ServiceVersionData {
        return coreRemoteApi.getServiceVersion(baseUrlProvider.getBaseUrl())
    }
}
