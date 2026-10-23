package com.docente.medidoraguainteligente.model

/**
 * Modelo que representa un Producto o Servicio del sistema de Agua Inteligente.
 */
data class ProductService(
    val id: String,
    val name: String,
    val category: String, // ej. "Planes de Agua", "Equipos IoT", "Mantenimiento"
    val description: String,
    val price: Double,
    val priceUnit: String, // ej. "$/mes", "$ único", "$/visita"
    val isAvailable: Boolean = true,
    var isSubscribed: Boolean = false
)
