package cn.afeibaili.afeinet.transfer.network.instance

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.data.PackageType
import cn.afeibaili.afeinet.common.logger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.GlobalEventExecutor
import java.io.Closeable
import java.util.concurrent.atomic.AtomicLong


/**
 * # 服务器隧道连接实例（Transfer）
 *
 * 创建一个隧道，通过服务端和隧道交流信息
 *
 * 开启一个TCP连接，接收用户连接并转发至服务端，再由服务端进行转发
 *
 * 如果连接断开将关闭此隧道
 *
 * @param instanceConfig 隧道信息
 * @param serverChannel 服务端消息流
 *
 * @author AfeiBaili
 * @version 2026/10/25 21:56
 */

class Instance(
    val instanceId: Int,
    val instanceConfig: InstanceConfig,
    val serverChannel: Channel,
) : Closeable {
    private val mianGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup(8)
    var inboundFlow: AtomicLong = AtomicLong(0L)
    var outboundFlow: AtomicLong = AtomicLong(0L)
    val userChannels: DefaultChannelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    var userConnectMap = UserConnectMap()
    val serverBootstrap: ServerBootstrap = ServerBootstrap()
    var future: ChannelFuture? = null

    fun start() {
        future = serverBootstrap.group(mianGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .handler(InstanceInitHandler(instanceId, serverChannel, instanceConfig, this, inboundFlow, outboundFlow))
            .childHandler(UserInitHandler(instanceId, serverChannel, userChannels, userConnectMap, outboundFlow))
            .bind(instanceConfig.transferPort)
            .addListener {
                if (!it.isSuccess) {
                    serverChannel.writeAndFlush(PackageType.Warn("启动[${instanceConfig.instanceName}]隧道失败: ${it.cause().message}"))
                    logger.warning("启动[${instanceConfig.transferHost}:${instanceConfig.transferPort}]失败")
                    it.cause().printStackTrace()
                }
            }
    }

    override fun close() {
        future?.channel()?.closeFuture()
        mianGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
        userChannels.close()
    }
}