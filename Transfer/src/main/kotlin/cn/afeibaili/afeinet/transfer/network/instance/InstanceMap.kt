package cn.afeibaili.afeinet.transfer.network.instance

import cn.afeibaili.afeinet.common.InstanceConfig
import io.netty.channel.Channel


/**
 * # 连接实例Map
 *
 * @author AfeiBaili
 * @version 2026/10/25 23:01
 */

class InstanceMap {
    val hashMap = HashMap<Int, Instance>()
    var indexId = 0

    fun forEach(action: (Instance) -> Unit) {
        hashMap.values.forEach(action)
    }

    fun putInstanceAndCreate(info: InstanceConfig, serverChannel: Channel): Instance {
        synchronized(hashMap) {
            val instance = Instance(indexId, info, serverChannel)
            hashMap.put(indexId, instance)
            indexId++
            return instance
        }
    }

    operator fun get(instanceId: Int): Instance? {
        return hashMap[instanceId]
    }
}