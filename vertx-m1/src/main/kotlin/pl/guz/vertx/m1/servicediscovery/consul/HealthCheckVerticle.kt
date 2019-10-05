package pl.guz.vertx.m1.servicediscovery.consul

import io.vertx.core.http.HttpHeaders
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import pl.guz.vertx.m1.Config

class HealthCheckVerticle(
        private val router: Router,
        private val config: Config
) : AbstractVerticle() {
    override fun start() {
        router.get(config.consulConfig.healthCheckPath).handler { event ->
            event.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(json {
                        obj("message" to "OK")
                    }.encodePrettily())
        }
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config.serverConfig.port)
    }
}