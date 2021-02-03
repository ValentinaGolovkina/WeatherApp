package com.valensiya.myapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterRecyclerView(private val context: Context,
                          private val list: Array<String>,
                          private val cityClickListener: CityClickListener
) : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val city: TextView = view.findViewById(R.id.city_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.city_item,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.city.text = data
        holder.itemView.setOnClickListener {
            cityClickListener.onCityClickListener(data)
        }
    }
}