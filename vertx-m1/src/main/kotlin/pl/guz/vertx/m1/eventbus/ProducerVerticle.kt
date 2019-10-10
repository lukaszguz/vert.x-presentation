package pl.guz.vertx.m1.eventbus

import io.vertx.core.json.Json
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.logger

class ProducerVerticle : AbstractVerticle() {

    override fun start() {
        vertx.eventBus().request<String>("jdd.consumer", Json.encode(Greetings("Hello JDD!"))) {
            logger.info("Message from consumer: {}", it.result().body())
        }

    }

    companion object {
        private val logger by logger()
    }
}

data class Greetings(val text: String)