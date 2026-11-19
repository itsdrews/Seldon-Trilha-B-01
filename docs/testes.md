# Plano de testes

## Estratégia

| Nível | Onde | O que cobre | Como rodar |
|---|---|---|---|
| Unitário (JVM) | `core/src/test` | Cada regra de segurança e o cálculo da nota | `./gradlew :core:test` |
| Unitário (JVM) | `app/src/test` | Textos da interface e acessibilidade (`UiText`) | `./gradlew :app:testDebugUnitTest` |
| Instrumentado | `app/src/androidTest` | **Pendente.** Coleta real do `DeviceSnapshotCollector` e tela em Compose | `./gradlew connectedDebugAndroidTest` |

Tudo que é unitário roda no CI a cada PR (`.github/workflows/ci.yml`).

## Casos críticos

| ID | Cenário | Resultado esperado | Teste |
|---|---|---|---|
| CT-01 | Aparelho com tudo em ordem | Nota 100, nível SEGURO, nenhum achado | `SecurityAnalyzerTest` |
| CT-02 | Sem bloqueio de tela | Achado HIGH, nota 70, nível ATENÇÃO | `RulesTest`, `SecurityAnalyzerTest` |
| CT-03 | Depuração USB ativa | Achado HIGH | `RulesTest` |
| CT-04 | Patch com exatamente 90 dias | **Sem** achado (limite) | `RulesTest` |
| CT-05 | Patch com 91 dias | Achado MEDIUM | `RulesTest` |
| CT-06 | Patch com mais de 180 dias | Achado HIGH | `RulesTest` |
| CT-07 | Data do patch ilegível | Achado `PATCH_UNKNOWN` (falha segura) | `RulesTest` |
| CT-08 | Indício de root | Nível CRÍTICO mesmo com nota 40 | `SecurityAnalyzerTest` |
| CT-09 | Todos os riscos juntos | Nota não fica negativa (0) | `SecurityAnalyzerTest` |
| CT-10 | Vários achados | Ordenados do mais grave para o menos grave | `SecurityAnalyzerTest` |
| CT-11 | Leitor de tela | Descrição da nota legível pelo TalkBack | `UiTextTest` |

## Critérios de aceite

- Todo PR para `develop` só entra com CI verde e uma revisão aprovada.
- Toda regra nova vem com pelo menos um teste do caso positivo e um do caso negativo.
- Regras com limite numérico (dias, versão) testam o valor exato do limite.

## Próximos passos

- Teste instrumentado do `DeviceSnapshotCollector` em emulador.
- Teste de UI em Compose (`createComposeRule`) para a `SecurityScreen`.
- Relatório de cobertura (JaCoCo/Kover) publicado no CI.
