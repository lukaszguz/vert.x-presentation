package pl.guz.vertx.m1.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.reactivex.core.AbstractVerticle
import pl.guz.vertx.m1.Config

class ServiceDiscoveryDeployer(private val config: Config) : AbstractVerticle() {

    override fun rxStart(): Completable {
        return vertx.rxDeployVerticle(ConsulServiceDiscovery(config.consulConfig))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(RegisterConsulServiceVerticle(config)))
                .ignoreElement()
    }
}