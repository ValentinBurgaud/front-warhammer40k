package com.magic.front.warhammer40k.startUp

import com.magic.front.warhammer40k.config.domain.Env
import org.flywaydb.core.Flyway
import org.springframework.stereotype.Component

@Component
class StartUp(val env: Env) {
    fun start() {
        Flyway.configure().dataSource(
            "jdbc:postgresql://${env.dataSourceConfig.host}:${env.dataSourceConfig.port}/${env.dataSourceConfig.db}",
            env.dataSourceConfig.username,
            env.dataSourceConfig.password
        )
            .load()
            .migrate()
    }
}