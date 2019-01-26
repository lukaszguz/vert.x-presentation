package pl.guz.vertx.m1.eventbus

import io.reactivex.Completable
import io.vertx.reactivex.core.AbstractVerticle

class EventBusDeployVerticle : AbstractVerticle() {
    override fun rxStart(): Completable {
        return vertx.rxDeployVerticle(ConsumerVerticle())
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(ProducerVerticle()))
                .ignoreElement()
    }
}