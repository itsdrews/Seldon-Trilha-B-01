package br.com.seldon.escudo.core

import java.time.LocalDate

val TODAY: LocalDate = LocalDate.of(2026, 11, 20)

/** Aparelho "ideal": nenhuma regra deve disparar. Cada teste altera só o campo que interessa. */
fun safeDevice() = DeviceSnapshot(
    screenLockEnabled = true,
    usbDebuggingEnabled = false,
    developerOptionsEnabled = false,
    securityPatchDate = TODAY.minusDays(30),
    rootIndicators = emptyList(),
    sdkInt = 34,
)
