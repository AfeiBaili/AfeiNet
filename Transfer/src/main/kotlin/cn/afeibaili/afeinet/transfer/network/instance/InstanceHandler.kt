package cn.afeibaili.afeinet.transfer.network.instance

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.data.PackageType
import cn.afeibaili.afeinet.common.logger
import cn.afeibaili.afeinet.common.util.byteSizeToUnitString
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.concurrent.atomic.AtomicLong


/**
 * # 实例处理器
 *
 * @author AfeiBaili
 * @version 2026/10/30 00:29
 */

class InstanceHandler(
    val instanceId: Int,
    val serverChannel: Channel,
    val instanceConfig: InstanceConfig,
    val instance: Instance,
    val inboundFlow: AtomicLong,
    val outboundFlow: AtomicLong,
) : ChannelInboundHandlerAdapter() {
    override fun channelActive(ctx: ChannelHandlerContext?) {
        logger.info(buildString {
            appendLine("${instanceConfig.instanceName} 🟢 正在开启")
            appendLine("\t备注: ${instanceConfig.remark}")
            appendLine("\t开放地址: ${instanceConfig.transferHost}:${instanceConfig.transferPort}")
        })
        serverChannel.write(PackageType.Info(buildString {
            appendLine("${instanceConfig.instanceName} 🟢 开启成功")
            appendLine("\t备注: ${instanceConfig.remark}")
            appendLine("\t监听端口: ${instanceConfig.listenerPort}")
            appendLine("\t开放地址: ${instanceConfig.transferHost}:${instanceConfig.transferPort}")
        }))
        serverChannel.write(PackageType.OpenInstance(instanceId, instanceConfig))
        serverChannel.flush()
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        val info: String = buildString {
            appendLine("${instanceConfig.instanceName}[${instanceConfig.transferPort}] 🔴 已关闭")
            appendLine("\t流量消耗: ${byteSizeToUnitString(inboundFlow.get() + outboundFlow.get())}")
            appendLine("\t入站消耗: ${byteSizeToUnitString(inboundFlow.get())}")
            appendLine("\t出站消耗: ${byteSizeToUnitString(outboundFlow.get())}")
        }
        logger.info(info)
        serverChannel.write(PackageType.Info(info))
        serverChannel.write(PackageType.CloseInstance(instanceId, instanceConfig))
        serverChannel.flush()
        instance.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        logger.fine(cause.stackTraceToString())
    }
}