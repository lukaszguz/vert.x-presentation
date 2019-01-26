package pl.guz.vertx.m2

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import pl.guz.vertx.m2.health.HealthCheckVerticle
import pl.guz.vertx.m2.http.HelloEndpointVerticle
import pl.guz.vertx.m2.servicediscovery.consul.ServiceDiscoveryDeployVerticle

class DeployVerticle(
        private val configuration: JsonObject
) : AbstractVerticle() {
    override fun rxStart(): Completable {
        val router = Router.router(vertx)
        return vertx.rxDeployVerticle(HealthCheckVerticle(router, configuration.getJsonObject("server")))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(ServiceDiscoveryDeployVerticle(configuration)))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(HelloEndpointVerticle(router, configuration)))
                .ignoreElement()
    }
}
