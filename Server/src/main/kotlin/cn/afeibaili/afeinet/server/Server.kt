package cn.afeibaili.afeinet.server

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.server.config.TransferConfig
import cn.afeibaili.afeinet.server.handler.ServerInitHandler
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel


/**
 * # 服务端连接
 *
 * @author AfeiBaili
 * @version 2026/10/26 19:35
 */

object Server {
    private val nioEventLoopGroup: NioEventLoopGroup = NioEventLoopGroup()
    private val bootstrap: Bootstrap = Bootstrap()
    private var future: ChannelFuture? = null

    fun start(tunnelInfos: Map<String, InstanceConfig>, transferConfig: TransferConfig) {
        future = bootstrap.group(nioEventLoopGroup)
            .channel(NioSocketChannel::class.java)
            .handler(ServerInitHandler(tunnelInfos, transferConfig))
            .connect(transferConfig.host, transferConfig.port)
            .addListener { if (!it.isSuccess) stop() }
            .sync()
    }

    fun stop() {
        nioEventLoopGroup.shutdownGracefully()
        future?.channel()?.closeFuture()
    }
}