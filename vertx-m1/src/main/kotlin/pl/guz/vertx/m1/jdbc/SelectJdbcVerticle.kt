package pl.guz.vertx.m1.jdbc

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import pl.guz.vertx.m1.logger

class SelectJdbcVerticle : AbstractVerticle() {
    private val logger by logger()
    private val selectAllSql = "select * from hello_table"

    override fun start() {
        val jdbcClient = JDBCClient.createShared(vertx, JsonObject(), "dataSource")
        jdbcClient.rxQueryStream(selectAllSql)
                .flatMapPublisher { sqlStream -> sqlStream.toFlowable().doAfterTerminate { sqlStream.close() } }
                .map { it.toDomainObject() }
                .doOnNext { logger.info("Row: {}", it) }
                .doOnComplete { logger.info("Finished") }
                .subscribe()
    }
}

private fun JsonArray.toDomainObject(): Hello {
    return Hello(getLong(0), getString(1))
}
