package cn.afeibaili.afeinet.server.handler

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.data.PackageParser
import cn.afeibaili.afeinet.common.data.PackageType
import cn.afeibaili.afeinet.common.logger
import cn.afeibaili.afeinet.server.Server
import cn.afeibaili.afeinet.server.instance.Instance
import cn.afeibaili.afeinet.server.instance.InstanceMap
import cn.afeibaili.afeinet.server.instance.localhost.LocalhostConnect
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter


/**
 * # 连接处理器
 *
 * @author AfeiBaili
 * @version 2026/10/27 19:56
 */

class ServerHandler(val tunnelInfos: Map<String, InstanceConfig>) : ChannelInboundHandlerAdapter() {
    val instanceMap = InstanceMap()
    lateinit var serverChannel: Channel
    val parser = PackageParser(
        info = { logger.info(it.info) },
        warn = { logger.warning(it.warn) },
        error = { logger.severe(it.error) },
        openConnect = { pkg ->
            val instance: Instance = instanceMap[pkg.instanceId] ?: return@PackageParser
            logger.fine("[${instance.instanceConfig.instanceName}]用户[${pkg.connectId}]连接, 用户id: ${pkg.connectId}")
            instance.addConnect(pkg.connectId)
        },
        closeConnect = { pkg ->
            val instance: Instance = instanceMap[pkg.instanceId] ?: return@PackageParser
            logger.fine("[${instance.instanceConfig.instanceName}]用户[${pkg.connectId}]断开, 用户id: ${pkg.connectId}")
            instance.closeConnect(pkg.connectId)
        },
        instanceData = { pkg ->
            logger.fine("收到转发端数据大小：${pkg.bytes.size}")
            val instance: Instance = instanceMap[pkg.instanceId] ?: return@PackageParser
            val connect: LocalhostConnect? = instance.getConnect(pkg.connectId)
            val localhostConnect: LocalhostConnect = connect ?: return@PackageParser
            localhostConnect.writeAndFlush(pkg.bytes)
        },
        openInstance = {
            logger.fine("已开启转发连接：${it.instanceConfig}")
            instanceMap.putInstance(it.instanceId, it.instanceConfig, serverChannel)
        },
        closeInstance = {
            logger.fine("已关闭转发连接：${it.instanceConfig}")
            instanceMap.removeInstance(it.instanceId)?.close()
        }
    )

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val pkg: PackageType = msg as PackageType
        parser.parse(pkg)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        serverChannel = ctx.channel()
        tunnelInfos.forEach { it -> ctx.channel().writeAndFlush(PackageType.Instance(it.value)) }
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        logger.info("远程服务器关闭")
        instanceMap.forEach { it.close() }
        Server.stop()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        logger.fine(cause.stackTraceToString())
    }
}