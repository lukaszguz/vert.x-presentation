package pl.guz.vertx.m1.eventbus

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.logger

class ConsumerVerticle : AbstractVerticle() {
    private val logger by logger()

    override fun start() {
        vertx.eventBus().consumer<JsonObject>("add") {
            logger.info("Got {}", it.body().encodePrettily())
            it.reply(JsonObject().put("message", "Hello too"))
        }
    }
}