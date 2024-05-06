package com.aspire.aquitoy.ui.requests.model

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.aspire.aquitoy.databinding.ItemsHistoryBinding
import com.aspire.aquitoy.ui.requests.RequestActivity

class HyperRequestViewHolder(private val binding: ItemsHistoryBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

    fun render(serviceInfo: ServiceInfo) {
        binding.idTitle.text = serviceInfo.serviceID
        binding.idFecha.text = serviceInfo.fecha

        // Abrir RequestActivity al hacer clic en el elemento del RecyclerView
        itemView.setOnClickListener {
            val intent = Intent(context, RequestActivity::class.java)
            intent.putExtra("patientName", serviceInfo.patientName)
            intent.putExtra("patientAge", serviceInfo.patientAge)
            intent.putExtra("patientCedula", serviceInfo.patientCedula)
            intent.putExtra("fecha", serviceInfo.fecha)
            intent.putExtra("nurseName", serviceInfo.nurseName)
            intent.putExtra("nurseCedula", serviceInfo.nurseCedula)
            intent.putExtra("medicalHistory", serviceInfo.medicalHistory)
            intent.putExtra("currentMedications", serviceInfo.currentMedications)
            // Agrega aquí más extras según sea necesario para mostrar toda la información del servicio
            context.startActivity(intent)
        }
    }
}
