package pl.guz.vertx.m1

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.codec.BodyCodec
import io.vertx.reactivex.servicediscovery.ServiceDiscovery
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint


class M2Client : AbstractVerticle() {
    override fun start() {


    }

    companion object {
        private val logger by logger()
    }
}
