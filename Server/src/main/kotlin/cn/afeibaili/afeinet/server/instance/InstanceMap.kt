package cn.afeibaili.afeinet.server.instance

import cn.afeibaili.afeinet.common.InstanceConfig
import io.netty.channel.Channel


/**
 * # 隧道Map
 *
 * @author AfeiBaili
 * @version 2026/10/31 23:49
 */

class InstanceMap {
    val hashMap = hashMapOf<Int, Instance>()

    fun putInstance(instanceId: Int, instanceConfig: InstanceConfig, serverChannel: Channel): Instance {
        val instance = Instance(instanceId, instanceConfig, serverChannel)
        hashMap[instanceId] = instance
        return instance
    }

    operator fun get(instanceId: Int): Instance? {
        return hashMap[instanceId]
    }

    fun removeInstance(instanceId: Int): Instance? {
        return hashMap.remove(instanceId)
    }

    fun forEach(action: (Instance) -> Unit) {
        hashMap.values.forEach(action)
    }
}