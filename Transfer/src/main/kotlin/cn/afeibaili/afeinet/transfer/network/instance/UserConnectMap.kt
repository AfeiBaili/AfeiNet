package cn.afeibaili.afeinet.transfer.network.instance

import io.netty.channel.Channel

/**
 * # 基于索引的HashMap
 *
 * @author AfeiBaili
 * @version 2026/10/25 22:51
 */

class UserConnectMap {
    private val hashMap = HashMap<Int, Channel>()
    private var index = 0

    fun putConnect(channel: Channel): Int {
        synchronized(hashMap) {
            index++
            hashMap.put(index, channel)
        }
        return index
    }

    operator fun get(id: Int): Channel? {
        return hashMap[id]
    }

    fun remove(id: Int): Channel? {
        return hashMap.remove(id)
    }

    fun size(): Int {
        return hashMap.size
    }
}