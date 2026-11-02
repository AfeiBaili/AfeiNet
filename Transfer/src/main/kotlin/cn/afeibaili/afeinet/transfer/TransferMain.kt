package cn.afeibaili.afeinet.transfer

fun main(args: Array<String>) {
    val map: Map<String, String> = args.associate {
        val split: List<String> = it.split("=")
        runCatching {
            Pair(split[0], split[1])
        }.getOrElse { it -> throw it }
    }

    val port: Int = map["--port"]?.toInt() ?: error("端口未指定，请使用--port参数")
    Transfer.start(port)
}