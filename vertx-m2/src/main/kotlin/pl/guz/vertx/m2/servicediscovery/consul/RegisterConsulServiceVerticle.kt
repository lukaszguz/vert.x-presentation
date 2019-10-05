package pl.guz.vertx.m2.servicediscovery.consul

import io.reactivex.Completable
import io.vertx.kotlin.ext.consul.checkOptionsOf
import io.vertx.kotlin.ext.consul.consulClientOptionsOf
import io.vertx.kotlin.ext.consul.serviceOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.consul.ConsulClient
import pl.guz.vertx.m2.Config
import pl.guz.vertx.m2.logger
import java.util.*

class RegisterConsulServiceVerticle(private val config: Config) : AbstractVerticle() {

    override fun rxStart(): Completable {
        val consulClient = ConsulClient.create(vertx, consulClientOptionsOf(
                defaultHost = config.consulConfig.host,
                defaultPort = config.consulConfig.port,
                dc = config.consulConfig.dc
        ))

        return consulClient.rxRegisterService(
                serviceOptionsOf(
                        id = UUID.randomUUID().toString(),
                        name = config.applicationName,
                        address = config.serverConfig.host,
                        port = config.serverConfig.port,
                        checkOptions = checkOptionsOf(
                                deregisterAfter = "10s",
                                interval = "10s",
                                http = "http://${config.serverConfig.host}:${config.serverConfig.port}${config.consulConfig.healthCheckPath}"
                        ),
                        tags = listOf("http-endpoint")
                ))
                .doOnComplete { logger.info("Services is registered") }
                .doOnError { logger.error("Service isn't registered", it) }
                .doFinally { consulClient.close() }
    }

    companion object {
        private val logger by logger()
    }
}