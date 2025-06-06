package com.talos.mexicanplacesrf.ui.adapters

import android.provider.Settings.Global.getString
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.talos.mexicanplacesrf.R
import com.talos.mexicanplacesrf.data.remote.model.PlaceDto
import com.talos.mexicanplacesrf.databinding.ActivityMainBinding
import com.talos.mexicanplacesrf.databinding.PlaceElementBinding

// Manejador de vistas requeridas
class PlaceViewHolder (
    private val binding: PlaceElementBinding

): RecyclerView.ViewHolder(binding.root) {

    fun bind(place: PlaceDto){

        binding.tvTitle.text = place.title
        binding.tvEstado.text = place.state
        binding.tvAttractions.text = place.attractions
        binding.tvUpdate.text = place.update

        Glide.with(binding.root.context)
            .load(place.thumbnail)
            .into(binding.ivThumbnail)

    }

}