package pl.guz.vertx.m1.servicediscovery.consul

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscoveryOptions

class ServiceDiscoveryConsul {
    companion object {
        fun create(vertx: Vertx, configuration: JsonObject): ServiceDiscovery {
            val consulConfig = configuration.getJsonObject("consul")
            return ServiceDiscovery.create(vertx, ServiceDiscoveryOptions()
                    .setBackendConfiguration(
                            JsonObject()
                                    .put("defaultHost", consulConfig.getString("host"))
                                    .put("dc", consulConfig.getString("dc"))
                    )
            )
        }

        fun enrichCheckOptions(httpRecord: Record, configuration: JsonObject): Record {
            return httpRecord.setMetadata(checkOptions(configuration))
        }

        fun checkOptions(configuration: JsonObject): JsonObject {
            val consulConfig = configuration.getJsonObject("consul")
            val consulAddress = consulConfig.getString("remoteHost")
            val serverPort = configuration.getJsonObject("server").getInteger("port")
            val checkPath = consulConfig.getJsonObject("healthCheck").getString("path")
            val checkUri = "$consulAddress:$serverPort/$checkPath"
            return JsonObject()
                    .put("checkoptions", JsonObject()
                            .put("deregisterAfter", "10s")
                            .put("interval", "10s")
                            .put("http", checkUri)
                    )
        }
    }
}