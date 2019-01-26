package pl.guz.vertx.m1.jdbc

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import pl.guz.vertx.m1.logger


class CreateTableJdbcVerticle(private val jdbcConfig: JsonObject) : AbstractVerticle() {
    private val logger by logger()
    private val createTableSql = "create table hello_table(id int primary key, name varchar(255))"

    override fun rxStart(): Completable {

        return Completable.complete()
    }
}
