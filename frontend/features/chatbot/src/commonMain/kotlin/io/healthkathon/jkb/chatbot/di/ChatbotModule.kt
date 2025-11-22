package io.healthkathon.jkb.chatbot.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.healthkathon.jkb.chatbot.data.ChatMockedRemoteApi
import io.healthkathon.jkb.chatbot.data.ChatbotRemoteApi
import io.healthkathon.jkb.chatbot.data.createChatbotRemoteApi
import io.healthkathon.jkb.core.data.remote.RemoteApiConfigProvider
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("io.healthkathon.jkb.chatbot")
object ChatBotModule {

    @Single
    fun provideChatbotRemoteApi(
        ktorfit: Ktorfit,
        config: RemoteApiConfigProvider
    ): ChatbotRemoteApi {
        return if (config.isMock()) {
            ChatMockedRemoteApi()
        } else {
            ktorfit.createChatbotRemoteApi()
        }
    }
}
