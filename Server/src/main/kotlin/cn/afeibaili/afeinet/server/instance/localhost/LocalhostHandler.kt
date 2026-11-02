package cn.afeibaili.afeinet.server.instance.localhost

import cn.afeibaili.afeinet.common.data.PackageType
import cn.afeibaili.afeinet.common.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter


/**
 * # 本地连接处理器
 *
 * @author AfeiBaili
 * @version 2026/11/2 14:36
 */

class LocalhostHandler(
    val instanceId: Int,
    val connectId: Int,
    val serverChannel: Channel,
    val connect: LocalhostConnect,
) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val buf: ByteBuf = msg as ByteBuf
        try {
            val byteSize: Int = buf.readableBytes()
            val data = ByteArray(byteSize)
            buf.readBytes(data)
            val dataPackage: PackageType.InstanceData = PackageType.InstanceData(instanceId, connectId, data)
            logger.fine("本地连接数据大小：${dataPackage.bytes.size}")
            serverChannel.writeAndFlush(dataPackage)
        } finally {
            buf.release()
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.fine("用户[$connectId]已连接本地服务器")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        connect.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
    }
}