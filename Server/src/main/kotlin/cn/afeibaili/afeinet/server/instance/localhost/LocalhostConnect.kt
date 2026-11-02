package cn.afeibaili.afeinet.server.instance.localhost

import cn.afeibaili.afeinet.common.InstanceConfig
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.io.Closeable


/**
 * # 本地连接
 *
 * @author AfeiBaili
 * @version 2026/11/2 16:23
 */

class LocalhostConnect(
    val instanceId: Int,
    val connectId: Int,
    val instanceConfig: InstanceConfig,
    val serverChannel: Channel,
) :
    Closeable {
    private val workerGroup = NioEventLoopGroup(2)
    private val bootstrap = Bootstrap()
    private var future: ChannelFuture? = null
    var channel: NioSocketChannel? = null

    fun start() {
        future = bootstrap.group(workerGroup)
            .channel(NioSocketChannel::class.java)
            .handler(LocalhostInitHandler(instanceId, connectId, serverChannel, this))
            .connect("127.0.0.1", instanceConfig.listenerPort)
            .sync()
    }

    override fun close() {
        workerGroup.shutdownGracefully()
        channel?.close()
        future?.channel()?.closeFuture()
    }

    fun writeAndFlush(data: ByteArray) {
        channel!!.writeAndFlush(data)
    }

    fun ready(ch: NioSocketChannel) {
        channel = ch
    }
}