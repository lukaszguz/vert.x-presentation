package pl.guz.vertx.m1

import io.reactivex.Completable
import io.vertx.core.DeploymentOptions
import io.vertx.core.VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import pl.guz.vertx.m1.eventbus.EventBusDeployVerticle
import pl.guz.vertx.m1.jdbc.JdbcDeployVerticle
import pl.guz.vertx.m1.servicediscovery.consul.HealthCheckVerticle
import pl.guz.vertx.m1.servicediscovery.consul.ServiceDiscoveryDeployer

class DeployVerticle(
        private val config: Config,
        private val configJson: JsonObject) : AbstractVerticle() {

    override fun rxStart(): Completable {
        val router = Router.router(vertx)
        return vertx.rxDeployVerticle(
                { HealthCheckVerticle(router, config) },
                DeploymentOptions()
                        .setInstances(DEFAULT_EVENT_LOOP_POOL_SIZE)
                        .setConfig(configJson.getJsonObject("server")
                        )
        ).ignoreElement()
                .andThen(vertx.rxDeployVerticle(EventBusDeployVerticle()))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(JdbcDeployVerticle(configJson)))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(ServiceDiscoveryDeployer(config)))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(M2Client()))
                .ignoreElement()
                .doOnComplete { logger.info("Everything is deployed") }
                .doOnError { logger.error("What is wrong!", it) }
    }

    companion object {
        private val logger by logger()
    }
}