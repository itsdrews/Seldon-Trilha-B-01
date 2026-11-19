package br.com.seldon.escudo.core

import java.time.LocalDate

/**
 * Fotografia do estado de segurança do aparelho em um instante.
 * O app (camada Android) coleta esses dados; o core só os avalia.
 * Manter este contrato livre de classes Android é o que permite testar as regras na JVM.
 */
data class DeviceSnapshot(
    val screenLockEnabled: Boolean,
    val usbDebuggingEnabled: Boolean,
    val developerOptionsEnabled: Boolean,
    /** Data do patch de segurança (Build.VERSION.SECURITY_PATCH). Nulo se não for possível ler. */
    val securityPatchDate: LocalDate?,
    /** Indícios de root encontrados (ex.: caminhos do binário `su`). Lista vazia = nenhum indício. */
    val rootIndicators: List<String>,
    val sdkInt: Int,
)

/** Gravidade de um achado. O peso é descontado da nota (100 = sem riscos encontrados). */
enum class Severity(val weight: Int) {
    LOW(5),
    MEDIUM(15),
    HIGH(30),
    CRITICAL(60),
}

/** Um risco encontrado, com a orientação para corrigi-lo. */
data class Finding(
    val id: String,
    val title: String,
    val severity: Severity,
    val recommendation: String,
)

enum class RiskLevel { SEGURO, ATENCAO, CRITICO }

data class SecurityReport(
    val findings: List<Finding>,
    val score: Int,
    val level: RiskLevel,
)
