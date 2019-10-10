package pl.guz.vertx.m1

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.VertxOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.micrometer.MicrometerMetricsOptions
import io.vertx.micrometer.VertxInfluxDbOptions
import io.vertx.reactivex.config.ConfigRetriever
import io.vertx.reactivex.core.Vertx
import java.net.InetAddress
import java.net.ServerSocket


class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
            Json.mapper.registerKotlinModule()

            val bootstrapVertx = Vertx.vertx()
            val retriever = createConfiguration(bootstrapVertx)
            retriever.getConfig { asyncResult ->
                val configuration = asyncResult.result()
                val config = extractConfiguration(configuration)
                bootstrapVertx.close {
                    val vertx = createVertx(config.influxConfig)
                    vertx.deployVerticle({ DeployVerticle(config, configuration) }, DeploymentOptions().setConfig(configuration))
                }
            }
        }

        private fun createConfiguration(bootstrapVertx: Vertx): ConfigRetriever {
            val local = ConfigStoreOptions()
                    .setType("file")
                    .setFormat("yaml")
                    .setConfig(JsonObject().put("path", "application.local.yml"))

            return ConfigRetriever.create(bootstrapVertx, ConfigRetrieverOptions()
                    .addStore(local)
            )
        }

        private fun extractConfiguration(config: JsonObject): Config {
            val consul = config.getJsonObject("consul")
            val influx = config.getJsonObject("influx")
            return Config(
                    applicationName = config.getString("application.name"),
                    serverConfig = ServerConfig(host = InetAddress.getLocalHost().hostAddress, port = config.getInteger("server.port", randomPort())),
                    consulConfig = ConsulConfig(
                            dc = consul.getString("dc", "dc1"),
                            host = consul.getString("host"),
                            port = consul.getInteger("port", 8500),
                            healthCheckPath = consul.getString("healthCheckPath")
                    ),
                    influxConfig = InfluxConfig(
                            enable = influx.getBoolean("enable"),
                            uri = influx.getString("uri"),
                            username = influx.getString("username"),
                            password = influx.getString("password"),
                            db = influx.getString("db")
                    )
            )
        }

        private fun randomPort() = ServerSocket(0).use { it.localPort }

        private fun createVertx(influxConfig: InfluxConfig): Vertx {
            return Vertx.vertx(VertxOptions()
                    .setMetricsOptions(MicrometerMetricsOptions()
                            .setInfluxDbOptions(VertxInfluxDbOptions()
                                    .setEnabled(influxConfig.enable)
                                    .setUri(influxConfig.uri)
                                    .setUserName(influxConfig.username)
                                    .setPassword(influxConfig.password)
                                    .setDb(influxConfig.db)
                            )
                            .setJvmMetricsEnabled(true)
                            .setEnabled(true)))
        }
    }
}
