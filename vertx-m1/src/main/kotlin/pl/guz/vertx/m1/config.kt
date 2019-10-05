package pl.guz.vertx.m1

data class Config(
        val applicationName: String,
        val serverConfig: ServerConfig,
        val consulConfig: ConsulConfig,
        val influxConfig: InfluxConfig
)

data class ServerConfig(
        val host: String,
        val port: Int
)

data class ConsulConfig(
        val dc: String,
        val host: String,
        val port: Int,
        val healthCheckPath: String
)

data class InfluxConfig(
        val enable: Boolean,
        val uri: String,
        val username: String,
        val password: String,
        val db: String
)