package pl.guz.vertx.m1.eventbus

import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.logger

class ConsumerVerticle : AbstractVerticle() {

    override fun start() {

    }

    companion object {
        private val logger by logger()
    }
}