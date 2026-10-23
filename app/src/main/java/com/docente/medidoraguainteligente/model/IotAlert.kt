package com.docente.medidoraguainteligente.model

/**
 * Modelo que representa una Alerta IoT del Medidor de Agua Inteligente.
 */
data class IotAlert(
    val id: String,
    val title: String,
    val timestamp: String,
    val severity: Severity,
    val description: String,
    var isProcessed: Boolean = false,
    val recommendedAction: String
) {
    enum class Severity {
        CRITICA, MODERADA, INFORMATIVA
    }
}
