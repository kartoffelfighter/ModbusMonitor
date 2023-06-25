package org.marcfischer.modbusmonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ControllersAdapter(
    private val controllers: MutableList<Controllers>
) : RecyclerView.Adapter<ControllersAdapter.ControllersViewHolder>() {

    class ControllersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControllersViewHolder {
        return ControllersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.controller_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ControllersViewHolder, position: Int) {
        val currentController = controllers[position]
        holder.itemView.apply{

        }
    }

    override fun getItemCount(): Int {
        return controllers.size
    }
}