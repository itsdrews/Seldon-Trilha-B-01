package br.com.seldon.escudo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.seldon.escudo.core.Finding
import br.com.seldon.escudo.core.RiskLevel
import br.com.seldon.escudo.core.SecurityReport
import br.com.seldon.escudo.core.Severity

@Composable
fun SecurityScreen(report: SecurityReport, onRescan: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "${report.score}/100",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.semantics {
                contentDescription = UiText.scoreContentDescription(report.score, report.level)
            },
        )
        Text(UiText.levelHeadline(report.level), style = MaterialTheme.typography.titleMedium)
        Button(onClick = onRescan) { Text("Analisar novamente") }
        Spacer(Modifier.height(8.dp))

        if (report.findings.isEmpty()) {
            Text("Nenhum risco encontrado.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(report.findings, key = { it.id }) { FindingCard(it) }
            }
        }
    }
}

@Composable
private fun FindingCard(finding: Finding) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Risco ${UiText.severityLabel(finding.severity)}",
                style = MaterialTheme.typography.labelMedium,
            )
            Text(finding.title, style = MaterialTheme.typography.titleSmall)
            Text(finding.recommendation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecurityScreenPreview() {
    MaterialTheme {
        SecurityScreen(
            report = SecurityReport(
                findings = listOf(
                    Finding("USB_DEBUG", "Depuração USB ativada", Severity.HIGH, "Desative quando não usar."),
                ),
                score = 70,
                level = RiskLevel.ATENCAO,
            ),
            onRescan = {},
        )
    }
}
