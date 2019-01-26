package pl.guz.vertx.m1

import io.reactivex.Completable
import io.vertx.core.DeploymentOptions
import io.vertx.core.VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.eventbus.EventBusDeployVerticle
import pl.guz.vertx.m1.health.HealthCheckVerticle
import pl.guz.vertx.m1.jdbc.JdbcDeployVerticle
import pl.guz.vertx.m1.servicediscovery.consul.ServiceDiscoveryDeployVerticle

class DeployVerticle(private val config: JsonObject) : AbstractVerticle() {
    override fun rxStart(): Completable {
        return vertx.rxDeployVerticle({ HealthCheckVerticle() },
                DeploymentOptions().setInstances(DEFAULT_EVENT_LOOP_POOL_SIZE)
                        .setConfig(config.getJsonObject("server"))
        ).ignoreElement()
                .andThen(vertx.rxDeployVerticle(EventBusDeployVerticle()))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(JdbcDeployVerticle(config)))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(ServiceDiscoveryDeployVerticle(config)))
                .ignoreElement()

    }
}