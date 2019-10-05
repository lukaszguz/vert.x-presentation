package pl.guz.vertx.m1

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.codec.BodyCodec
import io.vertx.reactivex.servicediscovery.ServiceDiscovery
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint


class M2Client : AbstractVerticle() {
    private val logger by logger()
    override fun start() {
        val serviceDiscovery = ServiceDiscovery.create(vertx)
        HttpEndpoint.rxGetWebClient(serviceDiscovery, JsonObject().put("name", "m2"))
                .flatMap { webClient ->
                    webClient.get("/hello")
                            .`as`(BodyCodec.jsonObject())
                            .rxSend()
                            .map(HttpResponse<JsonObject>::body)
                            .doOnSuccess { logger.info("Success: {}", it) }
                            .doOnError { logger.error("M2 is not responding") }
                }
                .doOnError { logger.error("M2 is not found", it) }
                .repeat(10)
                .subscribe()
    }
}
