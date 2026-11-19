package br.com.seldon.escudo.ui

import br.com.seldon.escudo.core.RiskLevel
import br.com.seldon.escudo.core.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiTextTest {

    @Test
    fun `toda gravidade tem rotulo em portugues`() {
        Severity.entries.forEach { assertTrue(UiText.severityLabel(it).isNotBlank()) }
    }

    @Test
    fun `descricao para leitor de tela inclui a nota`() {
        assertEquals(
            "Nota de segurança 70 de 100. Seu aparelho precisa de atenção.",
            UiText.scoreContentDescription(70, RiskLevel.ATENCAO),
        )
    }
}
