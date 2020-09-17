package com.codecool.holabusz.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Stop
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.row_item.view.*

class MainAdapter(private val stops: List<Stop>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MainViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 2020.09.01. itemview.idName would refer to the view

        /*
        val vehicleImage : Image = itemView.vehicleImage
        val vehicleShortName : String = itemView.vehicleShortName
        val minutes = itemView.minutes
         */

        val nameView = itemView.textView1
        val distanceView = itemView.textView2

        fun bind(stop : Stop) {
            // vehicleImage.setimage = vehicle.image
            // vehicleShortName.text = vehicle.shortName
            // minures = vehicle.minute


            nameView.text = stop.name
            distanceView.text = stop.distance.toInt().toString() + " m"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_item,parent,false)
        return MainViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = stops.get(position)
        when(holder) {
            is MainViewHolder -> { holder.bind(currentItem)}
        }
    }

    override fun getItemCount() = stops.size

}