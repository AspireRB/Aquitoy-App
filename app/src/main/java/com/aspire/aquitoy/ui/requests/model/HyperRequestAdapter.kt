package com.aspire.aquitoy.ui.requests.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.aspire.aquitoy.R

class HyperRequestAdapter(private val hyperServiceList: LiveData<List<ServiceInfo>>) : RecyclerView
.Adapter<HyperRequestViewHolder>()
{
    private var items: List<ServiceInfo> = emptyList()

    init {
        hyperServiceList.observeForever { newList ->
            items = newList ?: emptyList()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HyperRequestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HyperRequestViewHolder(layoutInflater.inflate(R.layout.items_history, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HyperRequestViewHolder, position: Int) {
        val item = items[position]
        holder.render(item)
    }
}
