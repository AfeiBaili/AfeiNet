package cn.afeibaili.afeinet.transfer.network.instance

import cn.afeibaili.afeinet.common.InstanceConfig
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.ServerSocketChannel
import java.util.concurrent.atomic.AtomicLong


/**
 * # 实例开启处理器
 *
 * @author AfeiBaili
 * @version 2026/10/26 13:22
 */

class InstanceInitHandler(
    val instanceId: Int,
    val serverChannel: Channel,
    val instanceConfig: InstanceConfig,
    val instance: Instance,
    val inboundFlow: AtomicLong,
    val outboundFlow: AtomicLong,
) : ChannelInitializer<ServerSocketChannel>() {
    override fun initChannel(ch: ServerSocketChannel) {
        ch.pipeline()
            .addLast(InstanceHandler(instanceId, serverChannel, instanceConfig, instance, inboundFlow, outboundFlow))
    }
}