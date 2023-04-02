package com.magic.front.warhammer40k

import com.magic.front.warhammer40k.startUp.StartUp
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Warhammer40kApplication

fun main(args: Array<String>) {
    runApplication<Warhammer40kApplication>(*args)
        .getBean(StartUp::class.java)
        .start()
}
