package pl.guz.vertx.m1.eventbus

import io.vertx.core.json.Json
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.logger

class ConsumerVerticle : AbstractVerticle() {

    override fun start() {

        vertx.eventBus().consumer<String>("jdd.consumer") {
            logger.info("Message from producer: {}", Json.decodeValue(it.body(), Greetings::class.java))
            it.reply("Siema!")
        }

    }

    companion object {
        private val logger by logger()
    }
}