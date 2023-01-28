package com.magic.front.warhammer40k.config

import com.magic.front.warhammer40k.config.domain.Env
import com.magic.front.warhammer40k.config.domain.apiKeyConfigBuilder
import com.magic.front.warhammer40k.config.domain.envBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class EnvConfig(
//        @Value("\${application.env}") val appEnv: String,
//        @Value("\${application.name}") val applicationName: String,
//
//        @Value("\${magicApi.baseUrl}") val magicApiBaseUrl: String,
) {

    @Bean
    @Primary
    fun _env(): Env {
        return envBuilder {
            env("dev")
            appName("api-magic")
            magicApiConfig(
                apiKeyConfigBuilder {
                    baseUrl("https://api.magicthegathering.io")
                }
            )
        }
    }
}
