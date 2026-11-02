package cn.afeibaili.afeinet.common.data

import cn.afeibaili.afeinet.common.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder


/**
 * # 包解码器
 *
 * @author AfeiBaili
 * @version 2026/10/29 00:19
 */

class PackageDecoder : ByteToMessageDecoder() {
    override fun decode(
        ctx: ChannelHandlerContext,
        inBuf: ByteBuf,
        out: MutableList<Any>,
    ) {
        if (inBuf.readableBytes() < Int.SIZE_BYTES) return
        val length: Int = inBuf.getInt(inBuf.readerIndex())
        if (length < 0) return Unit.let { ctx.close() }
        if (inBuf.readableBytes() < length + Int.SIZE_BYTES) return
        inBuf.skipBytes(Int.SIZE_BYTES)
        val data = ByteArray(length)
        inBuf.readBytes(data)
        runCatching {
            out.add(PackageType.fromByteArray(data))
        }.getOrElse {
            logger.fine(it.stackTraceToString())
            return
        }
    }
}