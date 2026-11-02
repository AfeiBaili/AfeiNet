package cn.afeibaili.afeinet.transfer.network.handler

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.data.PackageParser
import cn.afeibaili.afeinet.common.data.PackageType
import cn.afeibaili.afeinet.common.logger
import cn.afeibaili.afeinet.transfer.network.instance.Instance
import cn.afeibaili.afeinet.transfer.network.instance.InstanceMap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * # 服务端连接处理器
 *
 * 接收的服务器连接
 *
 * @author AfeiBaili
 * @version 2026/10/25 22:39
 */
class ServerHandler() : ChannelInboundHandlerAdapter() {
    val instancies = InstanceMap()
    lateinit var channel: Channel

    val parser = PackageParser(
        tunnel = { pkg ->
            val instanceConfig = InstanceConfig.fromByteArray(pkg.data)
            val instance: Instance = instancies.putInstanceAndCreate(instanceConfig, channel)
            instance.start()
        },
        info = { logger.info(it.info) },
        warn = { logger.warning(it.warn) },
        error = { logger.severe(it.error) },
        instanceData = { it ->
            val instance: Instance = instancies[it.instanceId] ?: return@PackageParser
            logger.fine("收到用户${it.connectId}[${instance.instanceConfig.instanceName}]服务端数据大小：${it.bytes.size}")
            val connectChannel: Channel = instance.userConnectMap[it.connectId] ?: return@PackageParser
            connectChannel.writeAndFlush(it.bytes)
        }
    )

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val pkg: PackageType = msg as PackageType
        parser.parse(pkg)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        channel = ctx.channel()
        logger.info("服务端连接进入: ${ctx.channel().remoteAddress()}")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        instancies.forEach { it.close() }
        logger.info("服务端连接断开: ${ctx.channel().remoteAddress()}")
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            if (evt.state() == IdleState.ALL_IDLE) {
                ctx.channel().writeAndFlush(PackageType.Heartbeat())
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        logger.fine(cause.stackTraceToString())
    }
}