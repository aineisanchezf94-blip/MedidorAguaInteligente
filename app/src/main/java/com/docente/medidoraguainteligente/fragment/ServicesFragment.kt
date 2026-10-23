package com.docente.medidoraguainteligente.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.docente.medidoraguainteligente.R
import com.docente.medidoraguainteligente.adapter.ServicesAdapter
import com.docente.medidoraguainteligente.model.ProductService

/**
 * Fragmento 2: Lista y Procesamiento de Productos y Servicios de Agua
 */
class ServicesFragment : Fragment() {

    private lateinit var rvServices: RecyclerView
    private lateinit var adapter: ServicesAdapter
    private val servicesList = mutableListOf<ProductService>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvServices = view.findViewById(R.id.rvServices)

        // Cargar catálogo inicial de productos y servicios
        if (servicesList.isEmpty()) {
            loadInitialServices()
        }

        adapter = ServicesAdapter(servicesList) { service ->
            processServiceAction(service)
        }

        rvServices.adapter = adapter
    }

    private fun loadInitialServices() {
        servicesList.addAll(
            listOf(
                ProductService(
                    id = "P01",
                    name = "Medidor Ultrasónico IoT HydroGuard Pro",
                    category = "Equipos IoT",
                    description = "Medidor inteligente de alta precisión con telemetría en tiempo real y detección automática de fugas.",
                    price = 149.99,
                    priceUnit = "pago único",
                    isSubscribed = false
                ),
                ProductService(
                    id = "P02",
                    name = "Plan Agua Residencial Inteligente (25 m³)",
                    category = "Planes de Agua",
                    description = "Suscripción mensual con monitoreo de consumo continuo y alertas preventivas vía app.",
                    price = 28.50,
                    priceUnit = "/ mes",
                    isSubscribed = true
                ),
                ProductService(
                    id = "P03",
                    name = "Servicio de Detección e Inspección de Fugas",
                    category = "Mantenimiento",
                    description = "Visita técnica especializada con escáner acoplado e inspección de presión en tuberías subterráneas.",
                    price = 45.00,
                    priceUnit = "/ visita",
                    isSubscribed = false
                ),
                ProductService(
                    id = "P04",
                    name = "Módulo de Válvula de Cierre Automático IoT",
                    category = "Equipos IoT",
                    description = "Válvula servomotorizada remota para corte automático preventivo desde la aplicación.",
                    price = 89.00,
                    priceUnit = "pago único",
                    isSubscribed = false
                ),
                ProductService(
                    id = "P05",
                    name = "Plan Negocio / Comercial (100 m³)",
                    category = "Planes de Agua",
                    description = "Plan avanzado para comercios con reporte diario de eficiencia hídrica y priorización técnica.",
                    price = 110.00,
                    priceUnit = "/ mes",
                    isSubscribed = false
                )
            )
        )
    }

    private fun processServiceAction(service: ProductService) {
        val actionTitle = if (service.isSubscribed) "Gestionar Servicio Activo" else "Procesar Solicitud de Servicio"
        val message = if (service.isSubscribed) {
            "El servicio '${service.name}' se encuentra activo.\n\n¿Desea cambiar la configuración o solicitar mantenimiento técnico?"
        } else {
            "Está a punto de contratar/procesar la orden de:\n• ${service.name}\n• Precio: $${service.price} ${service.priceUnit}\n\n¿Confirmar procesamiento?"
        }

        AlertDialog.Builder(requireContext())
            .setTitle(actionTitle)
            .setMessage(message)
            .setPositiveButton(if (service.isSubscribed) "Solicitar Mantenimiento" else "Confirmar Procesamiento") { _, _ ->
                service.isSubscribed = true
                adapter.notifyDataSetChanged()
                Toast.makeText(
                    requireContext(),
                    "¡Servicio '${service.name}' procesado exitosamente!",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
