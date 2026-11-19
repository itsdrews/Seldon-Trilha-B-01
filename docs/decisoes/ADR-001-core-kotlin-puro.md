# ADR-001: Regras de segurança em um módulo Kotlin puro

- **Status:** Aceita
- **Data:** 2026-11-19

## Contexto

O app avalia o estado de segurança do aparelho a partir de várias regras (bloqueio de tela, depuração USB, patch, root...). Essas regras são a parte mais importante do produto: um erro nelas significa dizer ao usuário que ele está seguro quando não está.

Ao mesmo tempo, a coleta dos dados depende de APIs do Android (`KeyguardManager`, `Settings.Global`, `Build`). Testar código que chama essas APIs exige emulador ou mocks pesados, o que é lento e frágil no CI.

## Decisão

Separar o projeto em dois módulos:

- **`core/`**: módulo Kotlin/JVM puro, sem dependência do Android. Contém o contrato `DeviceSnapshot`, as regras (`SecurityRule`) e o `SecurityAnalyzer`.
- **`app/`**: módulo Android. Só coleta os dados (`DeviceSnapshotCollector`) e exibe o resultado (Compose).

O `app` traduz o mundo Android em um `DeviceSnapshot` (um objeto de dados simples), e o `core` decide o que é risco.

## Alternativas consideradas

| Alternativa | Por que não |
|---|---|
| Tudo no módulo `app` | Testar regras exigiria Robolectric ou emulador. CI mais lento e regras misturadas com UI. |
| Módulo Android Library para o core | Funciona, mas traz o Android Gradle Plugin sem necessidade e testes mais lentos. |
| Arquitetura em camadas completa (data/domain/presentation) | Excessiva para o escopo atual. O desafio pede não criar módulos vazios só pela aparência. |

## Consequências

**Positivas**
- As regras são testadas em milissegundos na JVM (`./gradlew :core:test`), sem emulador.
- Cada regra é testável isoladamente, inclusive os limites (ex.: patch com exatamente 90 dias).
- Novas regras não exigem mexer na UI.

**Negativas**
- O `DeviceSnapshotCollector` (a coleta real) não é coberto por testes unitários; precisa de teste instrumentado no futuro.
- Toda informação nova do aparelho exige alterar o contrato `DeviceSnapshot` nos dois módulos.
