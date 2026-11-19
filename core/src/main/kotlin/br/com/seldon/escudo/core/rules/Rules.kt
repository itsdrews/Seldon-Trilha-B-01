package br.com.seldon.escudo.core.rules

import br.com.seldon.escudo.core.DeviceSnapshot
import br.com.seldon.escudo.core.Finding
import br.com.seldon.escudo.core.Severity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Contrato de uma regra: devolve um achado se houver risco, ou null se estiver tudo certo. */
fun interface SecurityRule {
    fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding?
}

fun defaultRules(): List<SecurityRule> = listOf(
    ScreenLockRule,
    UsbDebuggingRule,
    DeveloperOptionsRule,
    SecurityPatchRule(),
    RootRule,
    OutdatedAndroidRule(),
)

object ScreenLockRule : SecurityRule {
    override fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding? =
        if (snapshot.screenLockEnabled) null else Finding(
            id = "SCREEN_LOCK",
            title = "Bloqueio de tela desativado",
            severity = Severity.HIGH,
            recommendation = "Ative PIN, senha ou biometria em Configurações > Segurança.",
        )
}

object UsbDebuggingRule : SecurityRule {
    override fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding? =
        if (!snapshot.usbDebuggingEnabled) null else Finding(
            id = "USB_DEBUG",
            title = "Depuração USB ativada",
            severity = Severity.HIGH,
            recommendation = "Desative a depuração USB nas Opções do desenvolvedor quando não estiver usando.",
        )
}

object DeveloperOptionsRule : SecurityRule {
    override fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding? =
        if (!snapshot.developerOptionsEnabled) null else Finding(
            id = "DEV_OPTIONS",
            title = "Opções do desenvolvedor ativadas",
            severity = Severity.LOW,
            recommendation = "Desative as Opções do desenvolvedor se não precisar delas.",
        )
}

/**
 * Patch de segurança desatualizado.
 * Mais de [warnAfterDays] dias → MEDIUM; mais de [criticalAfterDays] dias → HIGH.
 * Data ilegível também é tratada como risco: sem a data, não dá para afirmar que está atualizado.
 */
class SecurityPatchRule(
    private val warnAfterDays: Long = 90,
    private val criticalAfterDays: Long = 180,
) : SecurityRule {
    override fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding? {
        val patch = snapshot.securityPatchDate ?: return Finding(
            id = "PATCH_UNKNOWN",
            title = "Data do patch de segurança desconhecida",
            severity = Severity.MEDIUM,
            recommendation = "Verifique atualizações em Configurações > Sistema > Atualização.",
        )
        val age = ChronoUnit.DAYS.between(patch, today)
        val severity = when {
            age > criticalAfterDays -> Severity.HIGH
            age > warnAfterDays -> Severity.MEDIUM
            else -> return null
        }
        return Finding(
            id = "PATCH_OUTDATED",
            title = "Patch de segurança com $age dias",
            severity = severity,
            recommendation = "Instale as atualizações de segurança pendentes do sistema.",
        )
    }
}

object RootRule : SecurityRule {
    override fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding? =
        if (snapshot.rootIndicators.isEmpty()) null else Finding(
            id = "ROOT",
            title = "Indícios de root encontrados",
            severity = Severity.CRITICAL,
            recommendation = "Aparelho com root perde proteções do sistema. Evite usar apps bancários nele.",
        )
}

/** Android abaixo de [minSupportedSdk] não recebe mais correções de segurança do Google. */
class OutdatedAndroidRule(private val minSupportedSdk: Int = 31) : SecurityRule {
    override fun evaluate(snapshot: DeviceSnapshot, today: LocalDate): Finding? =
        if (snapshot.sdkInt >= minSupportedSdk) null else Finding(
            id = "OLD_ANDROID",
            title = "Versão do Android sem suporte de segurança",
            severity = Severity.MEDIUM,
            recommendation = "Considere atualizar o sistema ou trocar de aparelho.",
        )
}
