package cn.afeibaili.afeinet.common

import java.nio.ByteBuffer


/**
 * # 隧道信息
 *
 * @author AfeiBaili
 * @version 2026/10/26 19:24
 */
data class InstanceConfig(
    val instanceName: String,
    val remark: String,
    val listenerPort: Int,
    val transferHost: String,
    val transferPort: Int,
) {
    fun toByteArray(): ByteArray {
        val instanceLength = instanceName.toByteArray().size
        val remarkLength = remark.toByteArray().size
        val transferLength = transferHost.toByteArray().size
        val buffer: ByteBuffer =
            ByteBuffer.allocate(
                Int.SIZE_BYTES + instanceLength
                        + Int.SIZE_BYTES + remarkLength
                        + Int.SIZE_BYTES
                        + Int.SIZE_BYTES + transferLength
                        + Int.SIZE_BYTES
            )
        buffer.putInt(instanceLength)
        buffer.put(instanceName.toByteArray())
        buffer.putInt(remarkLength)
        buffer.put(remark.toByteArray())
        buffer.putInt(listenerPort)
        buffer.putInt(transferLength)
        buffer.put(transferHost.toByteArray())
        buffer.putInt(transferPort)
        return buffer.array()
    }

    companion object {
        /**
         * ## 从字节数组中解析隧道信息
         *
         * @exception RuntimeException 如果拿到不存在的数据
         */
        fun fromByteArray(bytes: ByteArray): InstanceConfig {
            val buffer: ByteBuffer = ByteBuffer.wrap(bytes)
            buffer.rewind()
            val instanceName = String(ByteArray(buffer.getInt()).apply { buffer.get(this) })
            val remark = String(ByteArray(buffer.getInt()).apply { buffer.get(this) })
            val listenerPort = buffer.getInt()
            val transfer = String(ByteArray(buffer.getInt()).apply { buffer.get(this) })
            val transferPort = buffer.getInt()

            return InstanceConfig(instanceName, remark, listenerPort, transfer, transferPort)
        }
    }
}