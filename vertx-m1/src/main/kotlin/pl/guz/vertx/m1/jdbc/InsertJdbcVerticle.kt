package pl.guz.vertx.m1.jdbc

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.array
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import pl.guz.vertx.m1.logger

class InsertJdbcVerticle : AbstractVerticle() {
    private val logger by logger()
    private val insertIntoSql = "insert into hello_table values(?, ?)"

    override fun rxStart(): Completable {
        val jdbcClient = JDBCClient.createShared(vertx, JsonObject(), "dataSource")

        return jdbcClient.rxGetConnection()
                .flatMapCompletable { connection ->
                    connection.rxUpdateWithParams(insertIntoSql, Json.array(1, "hello1"))
                            .ignoreElement()
                            .andThen(connection.rxUpdateWithParams(insertIntoSql, Json.array(2, "hello2")))
                            .ignoreElement()
                            .andThen(connection.rxClose())
                }
                .doOnComplete { logger.info("Inserted rows") }
    }
}
