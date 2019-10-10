package pl.guz.vertx.m1.jdbc

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import pl.guz.vertx.m1.logger

class SelectJdbcVerticle : AbstractVerticle() {
    override fun start() {


    }

    companion object {
        private val logger by logger()
        private const val selectSql = "select * from jdd_users"
    }
}

private fun JsonArray.toDomainObject(): User {
    return User(id = getLong(0), name = getString(1))
}
