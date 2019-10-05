package pl.guz.vertx.m1.jdbc

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.array
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import io.vertx.reactivex.ext.sql.SQLConnection
import pl.guz.vertx.m1.logger


class CreateTableJdbcVerticle(private val config: JsonObject) : AbstractVerticle() {
    private val logger by logger()

    override fun rxStart(): Completable {
        val client = JDBCClient.createShared(
                vertx,
                config.getJsonObject("dataSource").getJsonObject("jdbc"),
                "dataSource"
        )

        return client.rxGetConnection()
                .flatMapCompletable { connection: SQLConnection ->
                    connection.rxExecute(createTableSql)
                            .andThen(
                                    connection.rxUpdateWithParams(insertSql, Json.array(1, "Hello")).ignoreElement()
                            )
                            .andThen(
                                    connection.rxUpdateWithParams(insertSql, Json.array(2, "Good Morning")).ignoreElement()
                            )
                            .andThen(connection.rxClose())
                }
                .doOnComplete { logger.info("Table was created") }
    }

    companion object {
        private const val createTableSql = "create table hello_table(id int primary key, name varchar(255))"
        private const val insertSql = "insert into hello_table values(?, ?)"
    }
}
