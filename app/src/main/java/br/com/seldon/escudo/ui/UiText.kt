package br.com.seldon.escudo.ui

import br.com.seldon.escudo.core.RiskLevel
import br.com.seldon.escudo.core.Severity

/** Textos da interface. Funções puras, testadas em app/src/test sem emulador. */
object UiText {

    fun severityLabel(severity: Severity): String = when (severity) {
        Severity.LOW -> "Baixo"
        Severity.MEDIUM -> "Médio"
        Severity.HIGH -> "Alto"
        Severity.CRITICAL -> "Crítico"
    }

    fun levelHeadline(level: RiskLevel): String = when (level) {
        RiskLevel.SEGURO -> "Seu aparelho está bem protegido"
        RiskLevel.ATENCAO -> "Seu aparelho precisa de atenção"
        RiskLevel.CRITICO -> "Seu aparelho está em risco"
    }

    /** Descrição para leitores de tela (TalkBack). */
    fun scoreContentDescription(score: Int, level: RiskLevel): String =
        "Nota de segurança $score de 100. ${levelHeadline(level)}."
}
