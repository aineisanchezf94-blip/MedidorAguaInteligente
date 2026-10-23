package com.docente.medidoraguainteligente.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.docente.medidoraguainteligente.R
import com.docente.medidoraguainteligente.adapter.AlertsAdapter
import com.docente.medidoraguainteligente.model.IotAlert
import com.google.android.material.button.MaterialButton

/**
 * Fragmento 3: Centro de Alertas IoT y Control de Válvula
 */
class AlertsFragment : Fragment() {

    private lateinit var rvAlerts: RecyclerView
    private lateinit var adapter: AlertsAdapter
    private lateinit var tvValveStatus: TextView
    private lateinit var btnToggleValve: MaterialButton

    private var isValveOpen = true
    private val alertsList = mutableListOf<IotAlert>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAlerts = view.findViewById(R.id.rvAlerts)
        tvValveStatus = view.findViewById(R.id.tvValveStatus)
        btnToggleValve = view.findViewById(R.id.btnToggleValve)

        updateValveUi()

        btnToggleValve.setOnClickListener {
            toggleValveState()
        }

        if (alertsList.isEmpty()) {
            loadInitialAlerts()
        }

        adapter = AlertsAdapter(alertsList) { alert ->
            processAlertAction(alert)
        }

        rvAlerts.adapter = adapter
    }

    private fun loadInitialAlerts() {
        alertsList.addAll(
            listOf(
                IotAlert(
                    id = "A01",
                    title = "Alerta Crítica: Fuga Detectada",
                    timestamp = "Hace 8 min",
                    severity = IotAlert.Severity.CRITICA,
                    description = "Se registró un consumo continuo inusual de 12 L/min por más de 30 min.",
                    recommendedAction = "Cerrar Válvula Principal Remotamente"
                ),
                IotAlert(
                    id = "A02",
                    title = "Presión de Red Anormal",
                    timestamp = "Hace 45 min",
                    severity = IotAlert.Severity.MODERADA,
                    description = "La presión interna de la tubería superó los 4.5 Bar (Límite máximo recomendado).",
                    recommendedAction = "Regular Válvula de Presión"
                ),
                IotAlert(
                    id = "A03",
                    title = "Límite Diario Alcanzado (80%)",
                    timestamp = "Hace 2 horas",
                    severity = IotAlert.Severity.INFORMATIVA,
                    description = "El hogar ha alcanzado 0.8 m³ del límite diario configurado de 1.0 m³.",
                    recommendedAction = "Ajustar Límite Diario de Consumo"
                )
            )
        )
    }

    private fun toggleValveState() {
        isValveOpen = !isValveOpen
        updateValveUi()
        val statusMsg = if (isValveOpen) "Válvula ABIERTA correctamente" else "Válvula CERRADA por seguridad"
        Toast.makeText(requireContext(), statusMsg, Toast.LENGTH_SHORT).show()
    }

    private fun updateValveUi() {
        if (isValveOpen) {
            tvValveStatus.text = "● ABIERTA (Flujo Normal)"
            tvValveStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_green))
            btnToggleValve.text = "Cerrar Válvula"
            btnToggleValve.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.alert_red))
        } else {
            tvValveStatus.text = "● CERRADA (Corte Preventivo)"
            tvValveStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_red))
            btnToggleValve.text = "Abrir Válvula"
            btnToggleValve.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.alert_green))
        }
    }

    private fun processAlertAction(alert: IotAlert) {
        AlertDialog.Builder(requireContext())
            .setTitle("Procesar Alerta IoT: ${alert.title}")
            .setMessage("¿Confirma la ejecución de la siguiente acción remota?\n\nAcción: ${alert.recommendedAction}")
            .setPositiveButton("Ejecutar Acción") { _, _ ->
                alert.isProcessed = true

                if (alert.severity == IotAlert.Severity.CRITICA) {
                    isValveOpen = false
                    updateValveUi()
                }

                adapter.notifyDataSetChanged()
                Toast.makeText(
                    requireContext(),
                    "Acción ejecutada correctamente para la alerta '${alert.title}'",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
