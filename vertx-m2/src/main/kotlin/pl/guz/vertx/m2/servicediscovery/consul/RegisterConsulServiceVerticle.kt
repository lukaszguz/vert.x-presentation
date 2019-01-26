package pl.guz.vertx.m2.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint


class RegisterConsulServiceVerticle(
        private val configuration: JsonObject
) : AbstractVerticle() {
    override fun rxStart(): Completable {
        val serviceDiscovery = ServiceDiscoveryConsul.create(vertx, configuration)

        val record = ServiceDiscoveryConsul.enrichCheckOptions(
                HttpEndpoint.createRecord(
                        "m2",
                        "127.0.0.1",
                        configuration.getJsonObject("server").getInteger("port"),
                        "/"
                ),
                configuration
        )

        return serviceDiscovery.rxPublish(record)
                .ignoreElement()
    }
}
