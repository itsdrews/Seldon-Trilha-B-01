package br.com.seldon.escudo.core

import br.com.seldon.escudo.core.rules.SecurityRule
import br.com.seldon.escudo.core.rules.defaultRules
import java.time.LocalDate

/**
 * Aplica as regras ao snapshot e calcula a nota de 0 a 100.
 *
 * - nota = 100 - soma dos pesos dos achados (mínimo 0)
 * - nota >= 80 → SEGURO; nota >= 50 → ATENCAO; abaixo disso → CRITICO
 * - qualquer achado CRITICAL força o nível CRITICO, independente da nota
 */
class SecurityAnalyzer(
    private val rules: List<SecurityRule> = defaultRules(),
) {
    fun analyze(snapshot: DeviceSnapshot, today: LocalDate): SecurityReport {
        val findings = rules
            .mapNotNull { it.evaluate(snapshot, today) }
            .sortedByDescending { it.severity.weight }

        val score = (100 - findings.sumOf { it.severity.weight }).coerceAtLeast(0)

        val level = when {
            findings.any { it.severity == Severity.CRITICAL } -> RiskLevel.CRITICO
            score >= 80 -> RiskLevel.SEGURO
            score >= 50 -> RiskLevel.ATENCAO
            else -> RiskLevel.CRITICO
        }

        return SecurityReport(findings, score, level)
    }
}
