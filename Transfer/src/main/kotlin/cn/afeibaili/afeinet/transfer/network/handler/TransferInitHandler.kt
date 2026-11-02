package cn.afeibaili.afeinet.transfer.network.handler

import cn.afeibaili.afeinet.common.logger
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.ServerSocketChannel


/**
 * # 转发端处理器
 *
 * @author AfeiBaili
 * @version 2026/10/25 22:30
 */

class TransferInitHandler(val port: Int) : ChannelInitializer<ServerSocketChannel>() {
    override fun initChannel(ch: ServerSocketChannel) {
        logger.info("转发服务器已启动至[$port]端口")
    }
}