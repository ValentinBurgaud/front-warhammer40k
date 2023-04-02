package com.magic.front.warhammer40k.config.domain

class CacheConfig private constructor(builder: Builder) {
    val ttl: Long
    val heap: Long

    init {
        ttl = builder.ttl
        heap = builder.heap
    }

    class Builder {
        internal var ttl: Long = 15
        internal var heap: Long = 100

        fun build(): CacheConfig {
            return CacheConfig(this)
        }

        fun ttl(ttl: Long): Builder {
            this.ttl = ttl
            return this
        }

        fun heap(heap: Long): Builder {
            this.heap = heap
            return this
        }
    }
}


inline fun cacheConfigBuilder(buildConfig: CacheConfig.Builder.() -> Unit): CacheConfig {
    val builder = CacheConfig.Builder()
    builder.buildConfig()
    return builder.build()
}
