package cn.afeibaili.afeinet.server.config

import cn.afeibaili.afeinet.common.InstanceConfig
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import java.io.File
import java.util.logging.Logger


/**
 * # 实例配置文件加载器
 *
 * @author AfeiBaili
 * @version 2026/10/26 19:41
 */

object ConfigLoader {
    val currentPath: String = System.getProperty("user.dir")
    private val jsonMapper: JsonMapper = JsonMapper.builder().addModule(kotlinModule()).build()
    private val logger = Logger.getLogger("ConfigLoader")

    init {
        val example = InstanceConfig("example name", "this is remark", 33393, "afeibaili.cn", 33393)
        val file = File(currentPath, "/config/example.json")
        if (file.parentFile.listFiles()?.isEmpty() == true) {
            file.parentFile.mkdirs()
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, example)
        }
    }

    fun loadInstance(insPath: String): InstanceConfig {
        val file = File(insPath)
        val info: InstanceConfig = jsonMapper.readValue<InstanceConfig>(file)
        logger.info("加载实例配置 $info")
        return info
    }

    fun loadByDirectory(insDir: String): MutableList<InstanceConfig> {
        val dir = File(insDir)
        val list = mutableListOf<InstanceConfig>()
        if (dir.isDirectory)
            dir.listFiles()?.forEach { list += loadInstance(it.canonicalPath) }
        return list
    }

    fun loadTransferConfig(path: String = "${System.getProperty("user.dir")}/transfer.json"): TransferConfig {
        val file = File(path)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(
                jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(TransferConfig("localhost", 33393))
            )
        }
        return jsonMapper.readValue<TransferConfig>(file)
    }
}