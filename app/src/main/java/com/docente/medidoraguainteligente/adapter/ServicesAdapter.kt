package com.docente.medidoraguainteligente.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.docente.medidoraguainteligente.R
import com.docente.medidoraguainteligente.model.ProductService
import com.google.android.material.button.MaterialButton

class ServicesAdapter(
    private var items: List<ProductService>,
    private val onProcessClick: (ProductService) -> Unit
) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    fun updateList(newItems: List<ProductService>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnProcessService: MaterialButton = itemView.findViewById(R.id.btnProcessService)

        fun bind(service: ProductService) {
            tvCategory.text = service.category.uppercase()
            tvName.text = service.name
            tvDescription.text = service.description
            tvPrice.text = "$ ${service.price} ${service.priceUnit}"

            if (service.isSubscribed) {
                tvStatus.text = "Activo"
                tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent_teal))
                btnProcessService.text = "Gestionar Servicio"
                btnProcessService.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_bg_elevated))
                btnProcessService.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent_teal))
            } else {
                tvStatus.text = if (service.isAvailable) "Disponible" else "Agotado"
                tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_secondary))
                btnProcessService.text = "Procesar Servicio"
                btnProcessService.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.accent_teal))
                btnProcessService.setTextColor(ContextCompat.getColor(itemView.context, R.color.bg_dark))
            }

            btnProcessService.setOnClickListener {
                onProcessClick(service)
            }
        }
    }
}
