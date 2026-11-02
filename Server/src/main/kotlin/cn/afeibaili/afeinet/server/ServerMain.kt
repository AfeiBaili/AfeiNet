package cn.afeibaili.afeinet.server

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.server.config.ConfigLoader


fun main(args: Array<String>) {
    val instanceConfigs =
        ConfigLoader.loadByDirectory("${ConfigLoader.currentPath}/config") as List<InstanceConfig>
    val transferConfig =
        ConfigLoader.loadTransferConfig()
    instanceConfigs.groupBy { it.instanceName }
        .forEach { if (it.value.size > 1) throw IllegalArgumentException("发现重名隧道") }

    val infoMap: Map<String, InstanceConfig> = instanceConfigs.associateBy { it.instanceName }
    Server.start(infoMap, transferConfig)
}