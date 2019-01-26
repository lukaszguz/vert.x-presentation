package pl.guz.vertx.m2.health

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router

class HealthCheckVerticle(
        private val router: Router,
        private val server: JsonObject
) : AbstractVerticle() {

    override fun start() {
        router.get("/health").handler { event ->
            event.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(json {
                        obj("message" to "OK")
                    }.encodePrettily())
        }

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(server.getInteger("port"))
    }
}
