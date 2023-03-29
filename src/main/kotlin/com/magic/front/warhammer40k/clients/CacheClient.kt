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

class CacheClient(
        private val cacheConfig: CacheConfig
) {
    private val cacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .withCache("refDocumentsTypesCache",
                    CacheConfigurationBuilder
                            .newCacheConfigurationBuilder(String::class.java, Map::class.java, ResourcePoolsBuilder.heap(cacheConfig.heap))
                            .withExpiry(ExpiryPolicyBuilder.noExpiration())
                            .build())
            .build(true)

    private val originsCacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("refOriginsCache",
            CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                    String::class.java,
                    String::class.java,
                    ResourcePoolsBuilder.heap(cacheConfig.heap)
                )
                .withExpiry(ExpiryPolicy.NO_EXPIRY)
                .build()
        )
        .build(true)

    private val magicCacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache("magicCardCache",
            CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String::class.java, Map::class.java, ResourcePoolsBuilder.heap(cacheConfig.heap))
                .withExpiry(ExpiryPolicyBuilder.noExpiration())
                .build())
        .build(true)

    val typesDocCache: Cache<String, Map<*, *>> = cacheManager.getCache("refDocumentsTypesCache", String::class.java, Map::class.java)
    val originsCache: Cache<String, String> = originsCacheManager.getCache("refOriginsCache", String::class.java, String::class.java)
    val cardsCache: Cache<String, List<*>> = magicCacheManager.getCache("magicCardCache", String::class.java, List::class.java)

    fun setUp() {
        DocTypes.values = typesDocCache["types"] as Map<String, String>
        DocTypesDefault.values = typesDocCache["typesDefault"] as Map<String, String>
        DocTypesClient.values = typesDocCache["typesClient"] as Map<String, String>
        DocTypesClaims.values = typesDocCache["typesGenericClaims"] as Map<String, String>
    }
}

object DocTypes {

    lateinit var values:  Map<String, String>

    val byCode = { code: String ->
        values.keys.firstOrNull { it.equals(code, true) }.option().getOrElseThrow { IllegalStateException("bad code $code") }
    }

    val valuesToString = { ->  values.map { "(code='${it.key}', label='${it.value}')" }.toVavrList() }

}

object DocTypesDefault  {

    lateinit var values:  Map<String, String>

    val byCode = { code: String ->
        values.keys.firstOrNull { it.equals(code, true) }.option().getOrElseThrow { IllegalStateException("bad code $code") }
    }

    val valuesToString = { ->  values.map { "(code='${it.key}', label='${it.value}')" }.toVavrList() }

}

object DocTypesClient {

    lateinit var values:  Map<String, String>

    val byCode = { code: String ->
        values.keys.firstOrNull { it.equals(code, true) }.option().getOrElseThrow { IllegalStateException("bad code $code") }
    }

    val valuesToString = { ->  values.map { "(code='${it.key}', label='${it.value}')" }.toVavrList() }

}

object DocTypesClaims {

    lateinit var values:  Map<String, String>

    val byCode = { code: String ->
        values.keys.firstOrNull { it.equals(code, true) }.option().getOrElseThrow { IllegalStateException("bad code $code") }
    }

    val valuesToString = { ->  values.map { "(code='${it.key}', label='${it.value}')" }.toVavrList() }

}
