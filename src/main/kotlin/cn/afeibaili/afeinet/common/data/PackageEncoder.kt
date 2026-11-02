package cn.afeibaili.afeinet.common.data

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder


/**
 * # 包编码器
 *
 * @author AfeiBaili
 * @version 2026/10/29 00:19
 */

class PackageEncoder : MessageToByteEncoder<PackageType>() {
    override fun encode(
        ctx: ChannelHandlerContext,
        msg: PackageType,
        out: ByteBuf,
    ) {
        val length: Int = msg.length()
        out.writeInt(length)
        out.writeBytes(msg.toByteArray())
    }
}