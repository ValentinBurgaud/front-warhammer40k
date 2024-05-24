package com.magic.front.warhammer40k.config

import com.magic.front.warhammer40k.handlers.CardHandler
import com.custom.lib.toolbox.extensions.mesure
import com.magic.front.warhammer40k.handlers.ImageHandler
import com.magic.front.warhammer40k.handlers.StaticListHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import java.net.URI

@Configuration
internal class RouterConfig {

    @Bean
    fun staticRoutes(
            cardHandler: CardHandler,
            imageHandler: ImageHandler,
            staticListHandler: StaticListHandler
    ): RouterFunction<ServerResponse> {
        return router {
            accept(TEXT_HTML).nest {
                GET("/") { permanentRedirect(URI("../swagger.html")).build() }
                GET("/swagger") { permanentRedirect(URI("../swagger.html")).build() }
                GET("/redoc") { permanentRedirect(URI("../redoc.html")).build() }
                resources("/**", ClassPathResource("/static/"))
            }
            "/api/v1/cards".nest {
                GET("", cardHandler::listCardWarhammer40k)
                "/{cardId}".nest {
                    GET("", cardHandler::getCardWarhammer40kById)
                }
            }
            "/api/v1/bdd/cards".nest {
                "".nest {
                    GET("", cardHandler::listCardBdd)
                    "/{cardId}".nest {
                        GET("", cardHandler::getCardBddById)
                        DELETE("", cardHandler::deleteCard)
                        PATCH("", cardHandler::updateCard)
                    }
                    contentType(MediaType.APPLICATION_JSON).nest {
                        POST("", cardHandler::createCard)
                    }
                    contentType(MediaType.MULTIPART_FORM_DATA).nest {
                        POST("", cardHandler::createCardWithImage)
                    }
                }
            }
            "/api/v1/images".nest {
                "/card/{cardId}".nest {
                    accept(MediaType.APPLICATION_OCTET_STREAM).nest {
                        GET("", imageHandler::downloadImage)
                    }
                }
            }
            "/api/v1/both/cards".nest {
                "".nest {
                    GET("", cardHandler::listCardBothSource)
                }
            }
            "/api/v1/cache/cards".nest {
                "".nest {
                    GET("", cardHandler::listCardWithCache)
                }
            }
            "/api/v1/static-lists".nest {
                GET("", staticListHandler::getColor)
            }
        }.mesure()
    }
}