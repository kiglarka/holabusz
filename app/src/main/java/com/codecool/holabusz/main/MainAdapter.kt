package com.codecool.holabusz.main

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Model

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var vehicles: List<Model> = ArrayList()

    class MainViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 2020.09.01. itemview.idName would refer to the view

        /*
        val vehicleImage : Image = itemView.vehicleImage
        val vehicleShortName : String = itemView.vehicleShortName
        val minutes = itemView.minutes

         */

        fun bind(model : Model) {
            // vehicleImage.setimage = vehicle.image
            // vehicleShortName.text = vehicle.shortName
            // minures = vehicle.minute
        }
    }

    fun submitList(vehicleList : List<Model>) { vehicles = vehicleList}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_main,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MainViewHolder -> { holder.bind(vehicles.get(position))}
        }
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }
}