package pl.guz.vertx.m2

import io.reactivex.Completable
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import pl.guz.vertx.m2.api.HelloEndpointVerticle
import pl.guz.vertx.m2.servicediscovery.consul.HealthCheckVerticle
import pl.guz.vertx.m2.servicediscovery.consul.ServiceDiscoveryDeployer

class DeployVerticle(private val config: Config) : AbstractVerticle() {

    override fun rxStart(): Completable {
        val router = Router.router(vertx)
        return vertx.rxDeployVerticle(HealthCheckVerticle(router, config))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(ServiceDiscoveryDeployer(config)))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(HelloEndpointVerticle(router, config.serverConfig)))
                .ignoreElement()
    }
}
