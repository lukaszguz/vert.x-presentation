package pl.guz.vertx.m1

import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonArray
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
                    val vertx = createVertx(configuration)
                    vertx.deployVerticle({ DeployVerticle(configuration) }, DeploymentOptions().setConfig(configuration))
                }
            }
        }

        private fun createConfiguration(bootstrapVertx: Vertx): ConfigRetriever {
            val local = ConfigStoreOptions()
                    .setType("file")
                    .setFormat("yaml")
                    .setConfig(JsonObject().put("path", "application.local.yml"))

            val git = ConfigStoreOptions()
                    .setType("git")
                    .setFormat("yaml")
                    .setConfig(JsonObject()
                            .put("url", "https://github.com/lukaszguz/vert.x-presentation-configuration.git")
                            .put("path", "local")
                            .put("filesets", JsonArray().add(JsonObject().put("pattern", "*-local.yml").put("format", "yaml"))))

            return ConfigRetriever.create(bootstrapVertx, ConfigRetrieverOptions()
                    .addStore(git)
                    .addStore(local)
            )
        }

        private fun createVertx(configuration: JsonObject): Vertx {
            val influxConfig = configuration.getJsonObject("influx")
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
    }
}
