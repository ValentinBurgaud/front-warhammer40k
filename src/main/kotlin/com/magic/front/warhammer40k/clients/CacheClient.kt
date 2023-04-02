package com.magic.front.warhammer40k.clients

import com.magic.front.warhammer40k.config.domain.CacheConfig
import com.magic.front.warhammer40k.model.Card
import io.vavr.kotlin.option
import io.vavr.kotlin.toVavrList
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.expiry.ExpiryPolicy
import org.reactivecouchbase.json.Json

class CacheClient(
        private val cacheConfig: CacheConfig
) {
    private val magicCacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("magicCardCache",
            CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String::class.java, List::class.java, ResourcePoolsBuilder.heap(cacheConfig.heap))
                .withExpiry(ExpiryPolicyBuilder.noExpiration())
                .build())
        .build(true)

    val cardsCache: Cache<String, List<*>> = magicCacheManager.getCache("magicCardCache", String::class.java, List::class.java)

    fun setUp() {
        CardsCache.values = cardsCache
    }
}

object CardsCache {
    lateinit var values: Cache<String, List<*>>
}
