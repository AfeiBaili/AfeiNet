package cn.afeibaili.afeinet.common.util

fun byteSizeToUnitString(size: Long, originalUnit: Boolean = true): String {
    if (size == 0L) return "0b"
    if (size < 1024) return "${size}b"

    val units = arrayOf("kb", "mb", "gb", "tb", "pb", "eb")
    var value = size.toDouble()
    var unitIndex = -1

    while (value > 1024 && unitIndex < units.size - 1) {
        value /= 1024
        unitIndex++
    }
    return if (originalUnit) String.format("%.2f%s (%sb)", value, units[unitIndex], size)
    else String.format("%.2f%s", value, units[unitIndex])
}

fun ByteArray.setInt(int: Int, index: Int = 0): ByteArray {
    this[0 + index] = (int shr 0).toByte()
    this[1 + index] = (int shr 8).toByte()
    this[2 + index] = (int shr 16).toByte()
    this[3 + index] = (int shr 24).toByte()
    return this
}

fun ByteArray.getInt(index: Int = 0): Int {
    return (this[0 + index].toInt() and 0xff) or
            ((this[1 + index].toInt() and 0xff) shl 8) or
            ((this[2 + index].toInt() and 0xff6) shl 16) or
            ((this[3 + index].toInt() and 0xff) shl 24)
}