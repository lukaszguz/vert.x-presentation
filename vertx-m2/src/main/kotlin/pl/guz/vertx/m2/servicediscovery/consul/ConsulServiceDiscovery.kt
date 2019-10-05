package pl.guz.vertx.m2.servicediscovery.consul

import io.vertx.core.Future
import io.vertx.kotlin.ext.consul.consulClientOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.Vertx
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.consul.ConsulServiceImporter
import pl.guz.vertx.m2.ConsulConfig

class ConsulServiceDiscovery(private val consulConfig: ConsulConfig) : AbstractVerticle() {

    private lateinit var serviceDiscovery: ServiceDiscovery

    override fun start(startFuture: Future<Void>) {
        create(vertx, consulConfig, startFuture)
    }

    private fun create(vertx: Vertx, consulConfig: ConsulConfig, future: Future<Void>) {
        serviceDiscovery = ServiceDiscovery.create(vertx.delegate)
                .registerServiceImporter(ConsulServiceImporter(),
                        consulClientOptionsOf(
                                dc = consulConfig.dc,
                                defaultHost = consulConfig.host,
                                defaultPort = consulConfig.port
                        ).toJson().put("scan-period", 1000),
                        future
                )
    }

    override fun stop() = serviceDiscovery.close()
}