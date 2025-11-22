package io.healthkathon.jkb.core.data.remote

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url
import io.healthkathon.jkb.core.data.model.ServiceVersionData

interface CoreRemoteApi {

    @GET
    suspend fun getServiceVersion(@Url url: String): ServiceVersionData
}
