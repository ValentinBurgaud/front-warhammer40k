package com.magic.front.warhammer40k.config.domain

class DataSourceConfig private constructor(builder: Builder) {

    val db: String
    val host: String
    val port: Int
    val username: String
    val password: String
    val maximumPoolSize: Int

    init {
        db = builder.db
        host = builder.host
        port = builder.port
        username = builder.username
        password = builder.password
        maximumPoolSize = builder.maximumPoolSize
    }

    class Builder {
        internal var db: String = ""
        internal var host: String = ""
        internal var port: Int = 5432
        internal var username: String = ""
        internal var password: String = ""
        internal var maximumPoolSize: Int = 10

        fun build(): DataSourceConfig {
            return DataSourceConfig(this)
        }

        fun db(db: String): Builder {
            this.db = db
            return this
        }

        fun host(host: String): Builder {
            this.host = host
            return this
        }

        fun port(port: Int): Builder {
            this.port = port
            return this
        }

        fun username(username: String): Builder {
            this.username = username
            return this
        }

        fun password(password: String): Builder {
            this.password = password
            return this
        }

        fun maximumPoolSize(maximumPoolSize: Int): Builder {
            this.maximumPoolSize = maximumPoolSize
            return this
        }
    }
}


inline fun dataSourceConfigBuilder(buildConfig: DataSourceConfig.Builder.() -> Unit): DataSourceConfig {
    val builder = DataSourceConfig.Builder()
    builder.buildConfig()
    return builder.build()
}
