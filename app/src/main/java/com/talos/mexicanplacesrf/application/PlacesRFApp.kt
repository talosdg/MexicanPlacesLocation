package com.talos.mexicanplacesrf.application

import android.app.Application
import com.talos.mexicanplacesrf.data.PlaceRepository
import com.talos.mexicanplacesrf.data.remote.RetrofitHelper

class PlacesRFApp: Application() {
    private val retrofit by lazy {
        RetrofitHelper().getRetrofit()
    }
    val repository by lazy {
        PlaceRepository(retrofit)
    }
}