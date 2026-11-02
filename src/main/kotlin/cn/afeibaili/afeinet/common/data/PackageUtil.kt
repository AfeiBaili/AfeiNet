package cn.afeibaili.afeinet.common.data

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.util.getInt
import cn.afeibaili.afeinet.common.util.setInt
import java.nio.ByteBuffer

fun createConnectPackage(instanceId: Int, connectId: Int): ByteArray {
    val capacity: Int = Int.SIZE_BYTES + Int.SIZE_BYTES
    val data = ByteArray(capacity)
    data.setInt(instanceId)
    data.setInt(connectId, Int.SIZE_BYTES)
    return data
}

fun getOpenConnectPackage(data: ByteArray): PackageType.OpenConnect {
    val instanceId: Int = data.getInt()
    val connectId: Int = data.getInt(Int.SIZE_BYTES)
    return PackageType.OpenConnect(instanceId, connectId)
}

fun getCloseConnectPackage(data: ByteArray): PackageType.CloseConnect {
    val instanceId: Int = data.getInt()
    val connectId: Int = data.getInt(Int.SIZE_BYTES)
    return PackageType.CloseConnect(instanceId, connectId)
}

fun createInstancePackage(instanceId: Int, instanceConfig: InstanceConfig): ByteArray {
    val infoData: ByteArray = instanceConfig.toByteArray()
    val buffer: ByteBuffer = ByteBuffer.allocate(Int.SIZE_BYTES + infoData.size)
    buffer.putInt(instanceId)
    buffer.put(infoData)
    return buffer.array()
}

fun createInstanceDataPackage(instanceId: Int, connectId: Int, data: ByteArray): ByteArray {
    val bytes = ByteArray(Int.SIZE_BYTES + Int.SIZE_BYTES + data.size)
    bytes.setInt(instanceId)
    bytes.setInt(connectId, Int.SIZE_BYTES)
    val data: ByteArray = data.copyInto(bytes, Int.SIZE_BYTES + Int.SIZE_BYTES)
    return data
}

fun getInstanceDataPackageByData(data: ByteArray): PackageType.InstanceData {
    val instanceId: Int = data.getInt()
    val connectId: Int = data.getInt(Int.SIZE_BYTES)
    val data: ByteArray = data.copyOfRange(Int.SIZE_BYTES + Int.SIZE_BYTES, data.size)
    return PackageType.InstanceData(instanceId, connectId, data)
}

fun getOpenInstancePackageByData(data: ByteArray): PackageType.OpenInstance {
    val instanceId: Int = data.getInt()
    val bytes: ByteArray = data.copyOfRange(Int.SIZE_BYTES, data.size)
    return PackageType.OpenInstance(instanceId, InstanceConfig.fromByteArray(bytes))
}

fun getCloseInstancePackageByData(data: ByteArray): PackageType.CloseInstance {
    val instanceId: Int = data.getInt()
    val bytes: ByteArray = data.copyOfRange(Int.SIZE_BYTES, data.size)
    return PackageType.CloseInstance(instanceId, InstanceConfig.fromByteArray(bytes))
}