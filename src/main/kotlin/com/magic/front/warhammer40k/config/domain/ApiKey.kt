package com.magic.front.warhammer40k.config.domain

class ApiKeyConfig private constructor(builder: Builder) {

    val baseUrl: String
    val appId: String
    val apiKey: String

    init {
        baseUrl = builder.baseUrl
        appId = builder.appId
        apiKey = builder.apiKey
    }

    class Builder {
        internal var baseUrl: String = ""
        internal var appId: String = ""
        internal var apiKey: String = ""


        fun build(): ApiKeyConfig {
            return ApiKeyConfig(this)
        }

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun appId(appId: String): Builder {
            this.appId = appId
            return this
        }

        fun apiKey(apiKey: String): Builder {
            this.apiKey = apiKey
            return this
        }
    }
}

inline fun apiKeyConfigBuilder(buildConfig: ApiKeyConfig.Builder.() -> Unit): ApiKeyConfig {
    val builder = ApiKeyConfig.Builder()
    builder.buildConfig()
    return builder.build()
}

