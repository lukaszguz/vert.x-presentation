package pl.guz.vertx.m1.eventbus

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle

class ConsumerVerticle : AbstractVerticle() {

    override fun start() {
        vertx.eventBus().consumer<JsonObject>("consumer") {
            val body = Json.decodeValue(it.body().encode(), Greetings::class.java)
            it.reply(json { obj("message" to "I got your message: $body") })
        }
    }
}