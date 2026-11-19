package br.com.seldon.escudo.core.rules

import br.com.seldon.escudo.core.Severity
import br.com.seldon.escudo.core.TODAY
import br.com.seldon.escudo.core.safeDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RulesTest {

    @Test
    fun `nenhuma regra dispara em aparelho seguro`() {
        defaultRules().forEach { rule ->
            assertNull("${rule::class.simpleName} não deveria disparar", rule.evaluate(safeDevice(), TODAY))
        }
    }

    @Test
    fun `sem bloqueio de tela gera achado HIGH`() {
        val finding = ScreenLockRule.evaluate(safeDevice().copy(screenLockEnabled = false), TODAY)
        assertEquals(Severity.HIGH, finding?.severity)
    }

    @Test
    fun `depuracao USB ativa gera achado HIGH`() {
        val finding = UsbDebuggingRule.evaluate(safeDevice().copy(usbDebuggingEnabled = true), TODAY)
        assertEquals(Severity.HIGH, finding?.severity)
    }

    @Test
    fun `opcoes do desenvolvedor ativas gera achado LOW`() {
        val finding = DeveloperOptionsRule.evaluate(safeDevice().copy(developerOptionsEnabled = true), TODAY)
        assertEquals(Severity.LOW, finding?.severity)
    }

    @Test
    fun `patch com exatamente 90 dias ainda e aceito`() {
        val device = safeDevice().copy(securityPatchDate = TODAY.minusDays(90))
        assertNull(SecurityPatchRule().evaluate(device, TODAY))
    }

    @Test
    fun `patch com 91 dias gera MEDIUM`() {
        val device = safeDevice().copy(securityPatchDate = TODAY.minusDays(91))
        assertEquals(Severity.MEDIUM, SecurityPatchRule().evaluate(device, TODAY)?.severity)
    }

    @Test
    fun `patch com mais de 180 dias gera HIGH`() {
        val device = safeDevice().copy(securityPatchDate = TODAY.minusDays(181))
        assertEquals(Severity.HIGH, SecurityPatchRule().evaluate(device, TODAY)?.severity)
    }

    @Test
    fun `patch ilegivel e tratado como risco`() {
        val finding = SecurityPatchRule().evaluate(safeDevice().copy(securityPatchDate = null), TODAY)
        assertEquals("PATCH_UNKNOWN", finding?.id)
    }

    @Test
    fun `indicio de root gera CRITICAL`() {
        val device = safeDevice().copy(rootIndicators = listOf("/system/xbin/su"))
        assertEquals(Severity.CRITICAL, RootRule.evaluate(device, TODAY)?.severity)
    }

    @Test
    fun `android abaixo do minimo suportado gera MEDIUM`() {
        val device = safeDevice().copy(sdkInt = 29)
        assertEquals(Severity.MEDIUM, OutdatedAndroidRule().evaluate(device, TODAY)?.severity)
    }
}
