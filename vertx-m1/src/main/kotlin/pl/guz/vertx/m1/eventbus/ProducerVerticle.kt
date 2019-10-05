package pl.guz.vertx.m1.eventbus

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.logger

class ProducerVerticle : AbstractVerticle() {
    private val logger by logger()

    override fun start() {
        vertx.eventBus().request<JsonObject>("consumer", JsonObject(Json.encode(Greetings("Hello JDD!")))) {
            val message = it.result()
            logger.info("Answer: {}", message.body())
        }
    }
}

data class Greetings(val text: String)