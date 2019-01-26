package pl.guz.vertx.m2.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint
import pl.guz.vertx.m2.logger


class RegisterConsulServiceVerticle(
        private val configuration: JsonObject
) : AbstractVerticle() {

    private val logger by logger()

    override fun rxStart(): Completable {
        val serviceDiscovery = ServiceDiscoveryConsul.create(vertx, configuration)

        val record = HttpEndpoint.createRecord(
                "m2",
                "127.0.0.1",
                configuration.getJsonObject("server").getInteger("port"),
                "/",
                ServiceDiscoveryConsul.checkOptions(configuration)
        )

        return serviceDiscovery.rxPublish(record)
                .doOnSuccess { logger.info("Published M2") }
                .doOnError { logger.info("Not published M2") }
                .ignoreElement()
    }
}
