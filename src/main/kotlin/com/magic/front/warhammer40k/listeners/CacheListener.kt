package com.magic.front.warhammer40k.listeners

import com.altima.lib.toolbox.extensions.logger
import com.magic.front.warhammer40k.services.CacheService
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service

class CacheMessage(source: Any, val message: String) : ApplicationEvent(source)

@Service
class CacheListener(private val cacheService: CacheService) : ApplicationListener<CacheMessage> {

    override fun onApplicationEvent(event: CacheMessage) {
        logger.info("got event $event")
        when (event.message) {
            "load-cards"-> cacheService.cacheCards()
                        .subscribe()
            else-> logger.info("do nothing !")
        }
    }
}
