package pl.guz.vertx.m1.jdbc

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.array
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import pl.guz.vertx.m1.logger


class CreateTableJdbcVerticle(private val config: JsonObject) : AbstractVerticle() {

    override fun rxStart(): Completable {
        return Completable.complete()
    }

    companion object {
        private val logger by logger()
        private const val createTableSql = "create table jdd_users(id int primary key, name varchar(255))"
        private const val insertSql = "insert into jdd_users values(?, ?)"
    }
}
