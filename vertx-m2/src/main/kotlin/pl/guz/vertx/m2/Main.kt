package pl.guz.vertx.m2

import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.micrometer.MicrometerMetricsOptions
import io.vertx.micrometer.VertxInfluxDbOptions
import io.vertx.reactivex.config.ConfigRetriever
import io.vertx.reactivex.core.Vertx


class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val bootstrapVertx = Vertx.vertx()
            val retriever = createConfiguration(bootstrapVertx)

            retriever.getConfig { asyncResult ->
                val configuration = asyncResult.result()
                bootstrapVertx.close {
                    val influxConfig = configuration.getJsonObject("influx")
                    val vertx = createVertx(influxConfig)
                    vertx.deployVerticle(DeployVerticle(configuration))
                }
            }
        }

        private fun createVertx(influxConfig: JsonObject): Vertx {
            return Vertx.vertx(VertxOptions()
                    .setMetricsOptions(MicrometerMetricsOptions()
                            .setInfluxDbOptions(VertxInfluxDbOptions()
                                    .setEnabled(true)
                                    .setUri(influxConfig.getString("uri"))
                                    .setUserName(influxConfig.getString("username"))
                                    .setPassword(influxConfig.getString("password"))
                                    .setDb(influxConfig.getString("db"))
                            )
                            .setJvmMetricsEnabled(true)
                            .setEnabled(true)))
        }

        private fun createConfiguration(bootstrapVertx: Vertx): ConfigRetriever {
            return ConfigRetriever.create(bootstrapVertx, ConfigRetrieverOptions()
                    .addStore(ConfigStoreOptions()
                            .setType("file")
                            .setFormat("yaml")
                            .setConfig(JsonObject().put("path", "application.local.yml"))))
        }
    }
}
