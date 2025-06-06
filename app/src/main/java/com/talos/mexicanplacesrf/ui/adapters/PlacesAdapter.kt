package com.talos.mexicanplacesrf.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.talos.mexicanplacesrf.data.remote.model.PlaceDto
import com.talos.mexicanplacesrf.databinding.PlaceElementBinding

class PlacesAdapter(
    private val places: List<PlaceDto>, // lista de lugares
    private val onPlaceClick: (PlaceDto) -> Unit // click en elemento
): RecyclerView.Adapter<PlaceViewHolder>() {
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): PlaceViewHolder {
        val binding = PlaceElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {

        val place = places[position]

        holder.bind(place)

        holder.itemView.setOnClickListener {
            onPlaceClick(place)
        }

    }

}