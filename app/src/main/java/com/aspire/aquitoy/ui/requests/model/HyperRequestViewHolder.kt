package com.aspire.aquitoy.ui.requests.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aspire.aquitoy.databinding.ItemsHistoryBinding

class HyperRequestViewHolder (view: View): RecyclerView.ViewHolder(view){

    val binding = ItemsHistoryBinding.bind(view)

    fun render(serviceInfo: ServiceInfo) {
        binding.idTitle.text = serviceInfo.serviceID
        binding.idFecha.text = serviceInfo.fecha

        itemView.setOnClickListener {

        }
    }
}