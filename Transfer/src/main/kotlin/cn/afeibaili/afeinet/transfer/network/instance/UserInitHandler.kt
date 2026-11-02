package cn.afeibaili.afeinet.transfer.network.instance

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.bytes.ByteArrayEncoder
import java.util.concurrent.atomic.AtomicLong


/**
 * # 用户连接初始化
 *
 * @author AfeiBaili
 * @version 2026/10/29 23:29
 */

class UserInitHandler(
    val instanceId: Int,
    val serverChannel: Channel,
    val userChannels: DefaultChannelGroup,
    val userConnectMap: UserConnectMap,
    val outboundFlow: AtomicLong,
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        userChannels.add(ch)
        ch.pipeline()
            .addLast(ByteArrayEncoder())
            .addLast(UserHandler(instanceId, serverChannel, userConnectMap, outboundFlow))
    }
}