package cn.afeibaili.afeinet.transfer.network.handler

import cn.afeibaili.afeinet.common.data.PackageDecoder
import cn.afeibaili.afeinet.common.data.PackageEncoder
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler

/**
 * # 服务端连接处理器
 *
 * 接收的服务器连接
 *
 * @author AfeiBaili
 * @version 2026/10/25 22:39
 */
class ServerInitHandler() : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
            .addLast(PackageEncoder())
            .addLast(PackageDecoder())
            .addLast(IdleStateHandler(0,0,60))
            .addLast(ServerHandler())
    }
}