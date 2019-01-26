package pl.guz.vertx.m1.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint
import pl.guz.vertx.m1.logger


class RegisterConsulServiceVerticle(
        private val configuration: JsonObject
) : AbstractVerticle() {
    private val logger by logger()

    override fun rxStart(): Completable {

        return Completable.complete()
    }
}
