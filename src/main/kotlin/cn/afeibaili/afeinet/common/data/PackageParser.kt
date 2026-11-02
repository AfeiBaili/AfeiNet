package cn.afeibaili.afeinet.common.data


/**
 * # 包处理器，方便处理包类型对象
 *
 * @author AfeiBaili
 * @version 2026/10/30 00:40
 */

class PackageParser(
    val command: (PackageType.Command) -> Unit = {},
    val instanceData: (PackageType.InstanceData) -> Unit = {},
    val tunnel: (PackageType.Instance) -> Unit = {},
    val info: (PackageType.Info) -> Unit = {},
    val warn: (PackageType.Warn) -> Unit = {},
    val error: (PackageType.Error) -> Unit = {},
    val heartbeat: (PackageType.Heartbeat) -> Unit = {},
    val openConnect: (PackageType.OpenConnect) -> Unit = {},
    val closeConnect: (PackageType.CloseConnect) -> Unit = {},
    val openInstance: (PackageType.OpenInstance) -> Unit = {},
    val closeInstance: (PackageType.CloseInstance) -> Unit = {},
) {
    fun parse(pkg: PackageType): Unit = when (pkg) {
        is PackageType.Command -> command(pkg)
        is PackageType.InstanceData -> instanceData(pkg)
        is PackageType.Instance -> tunnel(pkg)
        is PackageType.Info -> info(pkg)
        is PackageType.Warn -> warn(pkg)
        is PackageType.Error -> error(pkg)
        is PackageType.Heartbeat -> heartbeat(pkg)
        is PackageType.OpenConnect -> openConnect(pkg)
        is PackageType.CloseConnect -> closeConnect(pkg)
        is PackageType.OpenInstance -> openInstance(pkg)
        is PackageType.CloseInstance -> closeInstance(pkg)
    }
}