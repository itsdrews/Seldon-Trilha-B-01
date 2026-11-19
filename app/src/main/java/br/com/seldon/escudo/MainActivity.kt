package br.com.seldon.escudo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.com.seldon.escudo.core.SecurityAnalyzer
import br.com.seldon.escudo.ui.SecurityScreen
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private val analyzer = SecurityAnalyzer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val collector = DeviceSnapshotCollector(applicationContext)
        fun scan() = analyzer.analyze(collector.collect(), LocalDate.now())

        setContent {
            MaterialTheme {
                Surface {
                    var report by remember { mutableStateOf(scan()) }
                    SecurityScreen(report = report, onRescan = { report = scan() })
                }
            }
        }
    }
}
