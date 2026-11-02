package cn.afeibaili.afeinet.server.instance.localhost

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.bytes.ByteArrayEncoder

/**
 * # 本地连接初始化类
 *
 * @author AfeiBaili
 * @version 2026/11/2 14:35
 */

class LocalhostInitHandler(
    val instanceId: Int,
    val connectId: Int,
    val serverChannel: Channel,
    val connect: LocalhostConnect,
) : ChannelInitializer<NioSocketChannel>() {
    override fun initChannel(ch: NioSocketChannel) {
        connect.ready(ch)
        ch.pipeline()
            .addLast(ByteArrayEncoder())
            .addLast(LocalhostHandler(instanceId, connectId, serverChannel, connect))
    }
}