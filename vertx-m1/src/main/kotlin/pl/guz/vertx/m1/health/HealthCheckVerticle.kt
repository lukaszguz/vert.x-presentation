package pl.guz.vertx.m1.health

import io.vertx.core.http.HttpHeaders
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router

class HealthCheckVerticle : AbstractVerticle() {

    override fun start() {
        val router = Router.router(vertx)
        router.get("/health").handler { event ->
            event.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(json {
                        obj("message" to "OK")
                    }.encodePrettily())
        }

        vertx.createHttpServer()
                .requestHandler(router::handle)
                .listen(config().getInteger("port"))
    }
}
