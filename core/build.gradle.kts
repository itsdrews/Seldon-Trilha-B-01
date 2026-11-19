import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Módulo Kotlin puro: regras de segurança e contratos.
// Não depende do Android, então os testes rodam rápido na JVM, sem emulador.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    testImplementation(libs.junit)
}
