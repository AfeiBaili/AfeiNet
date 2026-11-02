package cn.afeibaili.afeinet.common.data

import cn.afeibaili.afeinet.common.InstanceConfig
import cn.afeibaili.afeinet.common.data.PackageEnum.*
import java.nio.ByteBuffer


/**
 * # 数据包信息
 *
 * @author AfeiBaili
 * @version 2026/10/26 13:15
 */

sealed class PackageType(val type: PackageEnum, val data: ByteArray) {
    //// INSTANCE //
    /** 实例数据 */
    class InstanceData(val instanceId: Int, val connectId: Int,val bytes: ByteArray) :
        PackageType(DATA, createInstanceDataPackage(instanceId, connectId, bytes))

    /** 实例信息 */
    class Instance(val instanceInfo: InstanceConfig) :
        PackageType(TUNNEL, instanceInfo.toByteArray())

    /** 开启实例 */
    class OpenInstance(val instanceId: Int, val instanceConfig: InstanceConfig) :
        PackageType(OPEN_INSTANCE, createInstancePackage(instanceId, instanceConfig))

    /** 关闭实例 */
    class CloseInstance(val instanceId: Int, val instanceConfig: InstanceConfig) :
        PackageType(CLOSE_INSTANCE, createInstancePackage(instanceId, instanceConfig))

    //// INFO //
    /** 命令 */
    class Command(val command: String) : PackageType(COMMAND, command.toByteArray())

    /** 日志信息 */
    class Info(val info: String) : PackageType(INFO, info.toByteArray())

    /** 日志警告信息 */
    class Warn(val warn: String) : PackageType(WARN, warn.toByteArray())

    /** 日志错误信息 */
    class Error(val error: String) : PackageType(ERROR, error.toByteArray())

    //// CONNECT //
    /** 心跳数据 */
    class Heartbeat() : PackageType(HEARTBEAT, ByteArray(0))

    /** 打开连接 */
    class OpenConnect(val instanceId: Int, val connectId: Int) :
        PackageType(OPEN_CONNECT, createConnectPackage(instanceId, connectId))

    /** 关闭连接 */
    class CloseConnect(val instanceId: Int, val connectId: Int) :
        PackageType(CLOSE_CONNECT, createConnectPackage(instanceId, connectId))

    /////////////////////////////////////////////////////////////////

    fun length() = Int.SIZE_BYTES + data.size
    fun dataToString() = String(data)

    fun toByteArray(): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(length())
        buffer.putInt(type.identifier)
        buffer.put(data)
        return buffer.array()
    }


    companion object {
        /**
         * ## 解析包数据
         *
         * @exception RuntimeException 如果存在不存在的包类型
         */
        fun fromByteArray(bytes: ByteArray): PackageType {
            val buffer: ByteBuffer = ByteBuffer.wrap(bytes)
            val type: Int = buffer.getInt()
            val packageEnum: PackageEnum = PackageEnum.match(type)
            val data = ByteArray(buffer.capacity() - Int.SIZE_BYTES).apply { buffer.get(this) }
            return when (packageEnum) {
                COMMAND -> Command(String(data))
                DATA -> getInstanceDataPackageByData(data)
                TUNNEL -> Instance(InstanceConfig.Companion.fromByteArray(data))
                INFO -> Info(String(data))
                WARN -> Warn(String(data))
                ERROR -> Error(String(data))
                HEARTBEAT -> Heartbeat()
                OPEN_CONNECT -> getOpenConnectPackage(data)
                CLOSE_CONNECT -> getCloseConnectPackage(data)
                OPEN_INSTANCE -> getOpenInstancePackageByData(data)
                CLOSE_INSTANCE -> getCloseInstancePackageByData(data)
            }
        }
    }
}

enum class PackageEnum(val identifier: Int) {
    COMMAND(0),
    DATA(1),
    TUNNEL(2),
    INFO(3),
    WARN(4),
    ERROR(5),
    HEARTBEAT(6),
    OPEN_CONNECT(7),
    CLOSE_CONNECT(8),
    OPEN_INSTANCE(9),
    CLOSE_INSTANCE(10),
    ;

    companion object {
        fun match(identifier: Int): PackageEnum = when (identifier) {
            0 -> COMMAND
            1 -> DATA
            2 -> TUNNEL
            3 -> INFO
            4 -> WARN
            5 -> ERROR
            6 -> HEARTBEAT
            7 -> OPEN_CONNECT
            8 -> CLOSE_CONNECT
            9 -> OPEN_INSTANCE
            10 -> CLOSE_INSTANCE
            else -> throw IllegalArgumentException("未知的包类型: $identifier")
        }
    }
}