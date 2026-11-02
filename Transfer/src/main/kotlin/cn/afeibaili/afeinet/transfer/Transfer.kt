package cn.afeibaili.afeinet.transfer

import cn.afeibaili.afeinet.transfer.network.handler.ServerInitHandler
import cn.afeibaili.afeinet.transfer.network.handler.TransferInitHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel


/**
 * # 转发服务器
 *
 * 服务器通信唯一接口，使用服务器连接此端口
 *
 * 连接创建时建立Instance对象，并开放交互端口
 *
 * @see cn.afeibaili.afeinet.network.instance.Instance
 * @author AfeiBaili
 * @version 2026/10/25 22:25
 */

object Transfer {
    private val parentEvent = NioEventLoopGroup(1)
    private val childEvent = NioEventLoopGroup()
    private val server = ServerBootstrap()
    private var future: ChannelFuture? = null

    fun start(port: Int) {
        future = server.group(parentEvent, childEvent)
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .handler(TransferInitHandler(port))
            .childHandler(ServerInitHandler())
            .bind(port).sync()
    }

    fun stop() {
        future?.channel()?.closeFuture()
        parentEvent.shutdownGracefully()
        childEvent.shutdownGracefully()
    }
}