package com.codecool.holabusz.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Departure
import kotlinx.android.synthetic.main.row_item.view.*

class DepartureAdapter(private val departures: ArrayList<Departure>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun clearAdapter(){
        departures.clear()
        notifyDataSetChanged()
    }

    fun setDepartures(departures: List<Departure>) {
        this.departures.clear()
        this.departures.addAll(departures)
        notifyDataSetChanged()
    }

    class DepartureViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 2020.09.01. itemview.idName would refer to the view

        val shortNameView = itemView.shortNameView
        val stopNameView = itemView.stopNameView
        val headerView = itemView.HeaderView
        val minuteView = itemView.textView2

        fun bind(departure: Departure) {

            shortNameView.text = departure.shortName
            shortNameView.setTextColor(Color.parseColor(departure.color))

            headerView.text = "► " + departure.stopHeadsign
            stopNameView.text = "@ " + departure.stopName
            var remainingTime = ((departure.departureTime - System.currentTimeMillis() / 1000) / 60)
            minuteView.text =
                if (remainingTime > 1) "$remainingTime perc"
                else "MOST"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return DepartureAdapter.DepartureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = departures.get(position)
        when (holder) {
            is DepartureAdapter.DepartureViewHolder -> {
                holder.bind(currentItem)
            }
        }
    }

    override fun getItemCount() = departures.size

}