package com.magic.front.warhammer40k.config.domain

class Env constructor(builder: Builder) {

    val env: String
    val appName: String
    val magicApiConfig: ApiKeyConfig
    val dataSourceConfig: DataSourceConfig
    val cacheConfig: CacheConfig

    init {
        env = builder.env
        appName = builder.appName
        magicApiConfig = builder.magicApiConfig
        dataSourceConfig = builder.dataSourceConfig
        cacheConfig = builder.cacheConfig
    }

    val isLocal = env == "local"

    class Builder {
        internal var env: String = ""
        internal var appName: String = ""
        internal var magicApiConfig: ApiKeyConfig = apiKeyConfigBuilder { }
        internal var dataSourceConfig: DataSourceConfig = dataSourceConfigBuilder { }
        internal var cacheConfig: CacheConfig = cacheConfigBuilder { }

        fun build(): Env {
            return Env(this)
        }

        fun env(env: String): Builder {
            this.env = env
            return this
        }

        fun appName(appName: String): Builder {
            this.appName = appName
            return this
        }

        fun magicApiConfig(magicApiConfig: ApiKeyConfig): Builder {
            this.magicApiConfig = magicApiConfig
            return this
        }

        fun dataSourceConfig(dataSourceConfig: DataSourceConfig): Builder {
            this.dataSourceConfig = dataSourceConfig
            return this
        }

        fun cacheConfig(cacheConfig: CacheConfig): Builder {
            this.cacheConfig = cacheConfig
            return this
        }
    }
}

internal inline fun envBuilder(buildConfig: Env.Builder.() -> Unit): Env {
    val builder = Env.Builder()
    builder.buildConfig()
    return builder.build()
}
