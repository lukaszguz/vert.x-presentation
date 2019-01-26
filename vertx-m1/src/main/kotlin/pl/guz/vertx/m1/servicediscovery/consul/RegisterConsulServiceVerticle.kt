package pl.guz.vertx.m1.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.servicediscovery.ServiceDiscovery
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint
import pl.guz.vertx.m1.logger


class RegisterConsulServiceVerticle(
        private val configuration: JsonObject
) : AbstractVerticle() {
    private val logger by logger()
    private lateinit var serviceDiscovery: ServiceDiscovery

    override fun rxStart(): Completable {
        serviceDiscovery = ServiceDiscoveryConsul.create(vertx, configuration)
        val record = ServiceDiscoveryConsul.enrichCheckOptions(
                HttpEndpoint.createRecord(
                        "m1",
                        "127.0.0.1",
                        configuration.getJsonObject("server").getInteger("port"),
                        "/"
                ),
                configuration)

        return serviceDiscovery.rxPublish(record)
                .doOnError { logger.error("M1 unsuccessfully published!", it) }
                .ignoreElement()
    }
}
