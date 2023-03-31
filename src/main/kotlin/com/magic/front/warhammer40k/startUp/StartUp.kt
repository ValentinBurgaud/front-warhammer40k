package com.magic.front.warhammer40k.startUp

import com.altima.lib.toolbox.extensions.logger
import com.magic.front.warhammer40k.config.domain.Env
import com.magic.front.warhammer40k.listeners.CacheMessage
import org.flywaydb.core.Flyway
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

@Component
class StartUp(val env: Env, val eventPublisher: EventPublisher) {
    fun start() {
        Flyway.configure().dataSource(
            "jdbc:postgresql://${env.dataSourceConfig.host}:${env.dataSourceConfig.port}/${env.dataSourceConfig.db}",
            env.dataSourceConfig.username,
            env.dataSourceConfig.password
        )
            .load()
            .migrate()
        eventPublisher.startUp()
    }
}

@Component
class EventPublisher(val eventPublisher: ApplicationEventPublisher) {

    fun startUp() {
        val everyFifteenMinutes: Long = 1000 * 60 * 15.toLong()
        Timer().scheduleAtFixedRate(0, everyFifteenMinutes) {
            logger.info("publishing load-cards")
            val customSpringEventTypes = CacheMessage(this, "load-cards")
            eventPublisher.publishEvent(customSpringEventTypes)
            logger.info("load-cards published")
        }
    }
}