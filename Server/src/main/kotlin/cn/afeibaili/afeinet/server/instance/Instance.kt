package cn.afeibaili.afeinet.server.instance

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.server.instance.localhost.LocalhostConnect
import io.netty.channel.Channel
import java.io.Closeable


/**
 * # 服务端实例（Server）
 *
 * @author AfeiBaili
 * @version 2026/11/1 00:54
 */

class Instance(val instanceId: Int, val instanceConfig: InstanceConfig, val serverChannel: Channel) :
    Closeable {

    private val localConnect = hashMapOf<Int, LocalhostConnect>()

    fun addConnect(connectId: Int): LocalhostConnect {
        val connect = LocalhostConnect(instanceId, connectId, instanceConfig, serverChannel)
        localConnect[connectId] = connect
        connect.start()
        return connect
    }

    fun closeConnect(connectId: Int) {
        val connect: LocalhostConnect = localConnect.remove(connectId) ?: return
        connect.close()
    }

    fun getConnect(connectId: Int): LocalhostConnect? {
        return localConnect[connectId]
    }

    override fun close() {
        localConnect.forEach { it.value.close() }
    }
}