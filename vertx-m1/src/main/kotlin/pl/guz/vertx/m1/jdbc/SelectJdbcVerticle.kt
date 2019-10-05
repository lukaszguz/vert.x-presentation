package pl.guz.vertx.m1.jdbc

import io.vertx.core.json.JsonArray
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import pl.guz.vertx.m1.logger

class SelectJdbcVerticle : AbstractVerticle() {
    private val logger by logger()

    override fun start() {
        val jdbcClient = JDBCClient.createShared(vertx, json { obj() }, "dataSource")
        jdbcClient.rxQueryStream(selectSql)
                .flatMapPublisher { sqlRowStream -> sqlRowStream.toFlowable().doAfterTerminate { sqlRowStream.close() } }
                .map { jsonArray -> jsonArray.toDomainObject() }
                .doOnNext { logger.info("Stream: $it") }
                .doOnComplete { logger.info("Finish read stream") }
                .subscribe()
    }

    companion object {
        private const val selectSql = "select * from hello_table"
    }
}

private fun JsonArray.toDomainObject(): Hello {
    return Hello(getLong(0), getString(1))
}
