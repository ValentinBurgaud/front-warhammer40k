package com.magic.front.warhammer40k.config

import com.magic.front.warhammer40k.clients.CacheClient
import com.magic.front.warhammer40k.clients.MagicClient
import com.magic.front.warhammer40k.config.domain.Env
import io.vertx.core.Vertx
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig(private val env: Env) {

    @Bean
    fun magicClients(): MagicClient {
        return MagicClient(env.magicApiConfig)
    }

    @Bean
    fun verticle(): Vertx {
        return Vertx.vertx()
    }

    @Bean
    fun cache(): CacheClient {
        return CacheClient(env.cacheConfig)
    }

    @Bean
    fun asyncClient(env: Env, verticle: Vertx): PgPool {
        return PgPool.pool(
            verticle,
            PgConnectOptions()
                .setPort(env.dataSourceConfig.port)
                .setHost(env.dataSourceConfig.host)
                .setDatabase(env.dataSourceConfig.db)
                .setUser(env.dataSourceConfig.username)
                .setPassword(env.dataSourceConfig.password)
                .setIdleTimeout(30),
            PoolOptions().setMaxSize(env.dataSourceConfig.maximumPoolSize)
        )
    }
}
