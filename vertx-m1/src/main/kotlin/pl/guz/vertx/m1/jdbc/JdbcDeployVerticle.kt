package pl.guz.vertx.m1.jdbc

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle

class JdbcDeployVerticle(private val config: JsonObject) : AbstractVerticle() {

    override fun rxStart(): Completable {
        return vertx.rxDeployVerticle(CreateTableJdbcVerticle(config))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(SelectJdbcVerticle()))
                .ignoreElement()
    }
}