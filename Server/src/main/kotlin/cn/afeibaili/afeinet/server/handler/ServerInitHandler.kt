package cn.afeibaili.afeinet.server.handler

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.data.PackageDecoder
import cn.afeibaili.afeinet.common.data.PackageEncoder
import cn.afeibaili.afeinet.common.logger
import cn.afeibaili.afeinet.server.config.TransferConfig
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel


/**
 * # 连接成功处理器
 *
 * @author AfeiBaili
 * @version 2026/10/27 19:53
 */

class ServerInitHandler(val tunnelInfos: Map<String, InstanceConfig>, val transferConfig: TransferConfig) :
    ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        logger.info("连接至[${transferConfig.host}:${transferConfig.port}]转发服务器成功")
        ch.pipeline()
            .addLast(PackageEncoder())
            .addLast(PackageDecoder())
            .addLast(ServerHandler(tunnelInfos))
    }

}