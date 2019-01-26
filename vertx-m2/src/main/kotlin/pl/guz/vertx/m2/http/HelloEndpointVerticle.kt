package pl.guz.vertx.m2.http

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router

class HelloEndpointVerticle(
        private val router: Router,
        private val config: JsonObject
) : AbstractVerticle() {
    override fun start() {
        router.get("/hello")
                .handler { request ->
                    request.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end(JsonObject().put("message", "hello from m2").encode())
                }

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config.getJsonObject("server").getInteger("port"))
    }
}