package pl.guz.vertx.m1.eventbus

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.logger

class ProducerVerticle : AbstractVerticle() {
    private val logger by logger()

    override fun start() {

        vertx.eventBus().send<JsonObject>("add", JsonObject().put("message", "hello")) {
            val result = it.result()
            logger.info("Got message: {}", result.body().encodePrettily())
        }
    }
}