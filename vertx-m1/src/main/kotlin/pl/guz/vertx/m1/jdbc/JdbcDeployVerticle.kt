package pl.guz.vertx.m1.jdbc

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle

class JdbcDeployVerticle(private val config: JsonObject) : AbstractVerticle() {

    override fun rxStart(): Completable {
        return vertx.rxDeployVerticle(CreateTableJdbcVerticle(config.getJsonObject("dataSource").getJsonObject("jdbc")))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(InsertJdbcVerticle()))
                .ignoreElement()
                .andThen(vertx.rxDeployVerticle(SelectJdbcVerticle()))
                .ignoreElement()
    }
}