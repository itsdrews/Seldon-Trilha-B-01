package br.com.seldon.escudo.core

import br.com.seldon.escudo.core.rules.SecurityRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityAnalyzerTest {

    private val analyzer = SecurityAnalyzer()

    @Test
    fun `aparelho seguro tem nota 100 e nivel SEGURO`() {
        val report = analyzer.analyze(safeDevice(), TODAY)
        assertEquals(100, report.score)
        assertEquals(RiskLevel.SEGURO, report.level)
        assertTrue(report.findings.isEmpty())
    }

    @Test
    fun `um achado HIGH derruba a nota para 70 e nivel ATENCAO`() {
        val report = analyzer.analyze(safeDevice().copy(screenLockEnabled = false), TODAY)
        assertEquals(70, report.score)
        assertEquals(RiskLevel.ATENCAO, report.level)
    }

    @Test
    fun `root forca nivel CRITICO mesmo com nota alta`() {
        val report = analyzer.analyze(safeDevice().copy(rootIndicators = listOf("su")), TODAY)
        assertEquals(40, report.score)
        assertEquals(RiskLevel.CRITICO, report.level)
    }

    @Test
    fun `nota nunca fica negativa`() {
        val worst = DeviceSnapshot(
            screenLockEnabled = false,
            usbDebuggingEnabled = true,
            developerOptionsEnabled = true,
            securityPatchDate = null,
            rootIndicators = listOf("su"),
            sdkInt = 26,
        )
        assertEquals(0, analyzer.analyze(worst, TODAY).score)
    }

    @Test
    fun `achados vem ordenados do mais grave para o menos grave`() {
        val device = safeDevice().copy(developerOptionsEnabled = true, usbDebuggingEnabled = true)
        val severities = analyzer.analyze(device, TODAY).findings.map { it.severity }
        assertEquals(listOf(Severity.HIGH, Severity.LOW), severities)
    }

    @Test
    fun `analyzer calcula a nota a partir das regras recebidas`() {
        // Regra falsa: sempre dispara, isolando o analyzer das regras concretas
        val fakeRule = SecurityRule { _, _ ->
            Finding(id = "FAKE", title = "Regra falsa", severity = Severity.MEDIUM, recommendation = "-")
        }
        val report = SecurityAnalyzer(listOf(fakeRule)).analyze(safeDevice(), TODAY)

        assertEquals(85, report.score)
        assertEquals(RiskLevel.SEGURO, report.level)
        assertEquals(listOf("FAKE"), report.findings.map { it.id })
    }
}
