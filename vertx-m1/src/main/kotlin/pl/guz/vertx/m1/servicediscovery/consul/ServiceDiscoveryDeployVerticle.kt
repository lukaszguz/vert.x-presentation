package pl.guz.vertx.m1.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle

class ServiceDiscoveryDeployVerticle(
        private val configuration: JsonObject
) : AbstractVerticle() {

    override fun rxStart(): Completable {
        return vertx.rxDeployVerticle(RegisterConsulServiceVerticle(configuration))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(M2Client()))
                .ignoreElement()
    }
}