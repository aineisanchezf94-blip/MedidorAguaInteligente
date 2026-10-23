package com.docente.medidoraguainteligente.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.docente.medidoraguainteligente.R
import com.docente.medidoraguainteligente.model.IotAlert
import com.google.android.material.button.MaterialButton

class AlertsAdapter(
    private var alerts: List<IotAlert>,
    private val onProcessAlertClick: (IotAlert) -> Unit
) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    fun updateList(newAlerts: List<IotAlert>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_iot_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alerts[position])
    }

    override fun getItemCount(): Int = alerts.size

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSeverityBadge: TextView = itemView.findViewById(R.id.tvSeverityBadge)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        private val tvAlertTitle: TextView = itemView.findViewById(R.id.tvAlertTitle)
        private val tvAlertDescription: TextView = itemView.findViewById(R.id.tvAlertDescription)
        private val btnProcessAlert: MaterialButton = itemView.findViewById(R.id.btnProcessAlert)

        fun bind(alert: IotAlert) {
            tvAlertTitle.text = alert.title
            tvAlertDescription.text = alert.description
            tvTimestamp.text = alert.timestamp

            when (alert.severity) {
                IotAlert.Severity.CRITICA -> {
                    tvSeverityBadge.text = "CRÍTICA"
                    tvSeverityBadge.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.alert_red))
                    btnProcessAlert.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.alert_red))
                }
                IotAlert.Severity.MODERADA -> {
                    tvSeverityBadge.text = "MODERADA"
                    tvSeverityBadge.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.alert_orange))
                    btnProcessAlert.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.alert_orange))
                }
                IotAlert.Severity.INFORMATIVA -> {
                    tvSeverityBadge.text = "INFO"
                    tvSeverityBadge.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.accent_blue))
                    btnProcessAlert.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.accent_blue))
                }
            }

            if (alert.isProcessed) {
                btnProcessAlert.text = "✓ Alerta Procesada y Resuelta"
                btnProcessAlert.setBackgroundColor(Color.parseColor("#1D2A4A"))
                btnProcessAlert.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent_teal))
                btnProcessAlert.isEnabled = false
            } else {
                btnProcessAlert.text = "Ejecutar: ${alert.recommendedAction}"
                btnProcessAlert.setTextColor(Color.WHITE)
                btnProcessAlert.isEnabled = true
                btnProcessAlert.setOnClickListener {
                    onProcessAlertClick(alert)
                }
            }
        }
    }
}
