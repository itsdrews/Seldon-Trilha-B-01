package br.com.seldon.escudo

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import br.com.seldon.escudo.core.DeviceSnapshot
import java.io.File
import java.time.LocalDate

/**
 * Única classe que conversa com as APIs do Android.
 * Lê o estado do aparelho e entrega um [DeviceSnapshot] para o core avaliar.
 */
class DeviceSnapshotCollector(private val context: Context) {

    fun collect(): DeviceSnapshot = DeviceSnapshot(
        screenLockEnabled = context.getSystemService(KeyguardManager::class.java)?.isDeviceSecure ?: false,
        usbDebuggingEnabled = globalSettingOn(Settings.Global.ADB_ENABLED),
        developerOptionsEnabled = globalSettingOn(Settings.Global.DEVELOPMENT_SETTINGS_ENABLED),
        securityPatchDate = parsePatchDate(Build.VERSION.SECURITY_PATCH),
        rootIndicators = SU_PATHS.filter { File(it).exists() },
        sdkInt = Build.VERSION.SDK_INT,
    )

    private fun globalSettingOn(name: String): Boolean =
        Settings.Global.getInt(context.contentResolver, name, 0) == 1

    private fun parsePatchDate(raw: String?): LocalDate? =
        runCatching { LocalDate.parse(raw) }.getOrNull()

    private companion object {
        // Heurística simples: detecção de root pode ser burlada (ver docs/architecture.md).
        val SU_PATHS = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/app/Superuser.apk",
            "/data/local/xbin/su",
        )
    }
}
