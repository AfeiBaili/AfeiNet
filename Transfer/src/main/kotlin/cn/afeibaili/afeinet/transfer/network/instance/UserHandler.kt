package cn.afeibaili.afeinet.transfer.network.instance

import cn.afeibaili.afeinet.common.data.PackageType
import cn.afeibaili.afeinet.common.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.concurrent.atomic.AtomicLong


/**
 * # 连接处理器
 *
 * @author AfeiBaili
 * @version 2026/10/26 13:21
 */

class UserHandler(
    val instanceId: Int,
    val serverChannel: Channel,
    val userConnectMap: UserConnectMap,
    val outboundFlow: AtomicLong,
) :
    ChannelInboundHandlerAdapter() {
    var id: Int = -1

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val buf = msg as ByteBuf
        try {
            val length: Int = buf.readableBytes()
            val data = ByteArray(length)
            outboundFlow.addAndGet(length.toLong())
            buf.readBytes(data)
            val instanceData: PackageType.InstanceData = PackageType.InstanceData(instanceId, id, data)
            logger.fine("用户${id}连接发送数据包，大小: ${instanceData.bytes.size}")
            serverChannel.writeAndFlush(instanceData)
        } finally {
            buf.release()
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        id = userConnectMap.putConnect(ctx.channel())
        logger.fine("用户${id}连接进入: ${ctx.channel().remoteAddress()}")
        serverChannel.writeAndFlush(PackageType.OpenConnect(instanceId, id))
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.fine("用户${id}连接断开: ${ctx.channel().remoteAddress()}")
        userConnectMap.remove(id)
        serverChannel.writeAndFlush(PackageType.CloseConnect(instanceId, id))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.fine("用户${id}连接抛出异常: ${cause.stackTraceToString()}")
        ctx.close()
    }
}