package pl.guz.vertx.m2.api

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import pl.guz.vertx.m2.ServerConfig


class HelloEndpointVerticle(
        private val router: Router,
        private val serverConfig: ServerConfig
) : AbstractVerticle() {

    override fun start() {

    }
}