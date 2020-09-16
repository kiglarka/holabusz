package com.codecool.holabusz.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Departure
import com.codecool.holabusz.model.Stop
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.row_item.view.*

class DepartureAdapter(private val departures: List<Departure>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DepartureViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 2020.09.01. itemview.idName would refer to the view

        /*
        val vehicleImage : Image = itemView.vehicleImage
        val vehicleShortName : String = itemView.vehicleShortName
        val minutes = itemView.minutes
         */

        val nameView = itemView.textView1
        val minuteView = itemView.textView2

        fun bind(departure: Departure) {
            // vehicleImage.setimage = vehicle.image
            // vehicleShortName.text = vehicle.shortName
            // minures = vehicle.minute


            nameView.text = "► " + departure.stopHeadsign
            var remainingTime = ((departure.departureTime - System.currentTimeMillis()/1000)/60)
            minuteView.text =
                if ( remainingTime>1 ) remainingTime.toString() + " perc"
                else "MOST"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_item,parent,false)
        return DepartureAdapter.DepartureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = departures.get(position)
        when(holder) {
            is DepartureAdapter.DepartureViewHolder -> { holder.bind(currentItem)}
        }
    }

    override fun getItemCount() = departures.size

}