package pl.guz.vertx.m1

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.codec.BodyCodec
import io.vertx.reactivex.servicediscovery.ServiceDiscovery
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint


class M2Client : AbstractVerticle() {
    override fun start() {

        val serviceDiscovery = ServiceDiscovery.create(vertx)
        HttpEndpoint.rxGetWebClient(serviceDiscovery, JsonObject().put("name", "m2"))
                .flatMap { webClient ->
                    webClient.get("/hello")
                            .`as`(BodyCodec.json(Response::class.java))
                            .rxSend()
                            .doOnSuccess { logger.info("Message from M2: {}", it.body()) }
                }
                .subscribe()

    }

    companion object {
        private val logger by logger()
    }
}

data class Response(val message: String)