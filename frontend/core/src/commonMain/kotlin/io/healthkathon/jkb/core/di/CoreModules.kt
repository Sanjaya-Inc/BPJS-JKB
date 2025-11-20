package io.healthkathon.jkb.core.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.healthkathon.jkb.core.data.LocalDataSource
import io.healthkathon.jkb.core.data.createLocalDataSource
import io.healthkathon.jkb.core.data.remote.KtorfitCreator
import io.healthkathon.jkb.core.data.util.JsonParserCreator
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@ComponentScan("id.healthkathon.jkb.core")
@Module
object CoreModules {
    @Single
    fun provideJsonParser(jsonParserCreator: JsonParserCreator): Json {
        return jsonParserCreator.create()
    }

    @Single
    fun provideLocalDataSource(): LocalDataSource {
        return createLocalDataSource()
    }

    @Single
    fun provideKtorfit(creator: KtorfitCreator): Ktorfit {
        return creator.create()
    }
}
