package com.magic.front.warhammer40k.config

import com.magic.front.warhammer40k.clients.MagicClient
import com.magic.front.warhammer40k.config.domain.Env
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig(private val env: Env) {

    @Bean
    fun magicClients(): MagicClient {
        return MagicClient(env.magicApiConfig)
    }
}
