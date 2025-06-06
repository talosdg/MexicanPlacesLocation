package com.talos.mexicanplacesrf.data

import com.talos.mexicanplacesrf.data.remote.PlacesApi
import com.talos.mexicanplacesrf.data.remote.model.PlaceDetailDto
import com.talos.mexicanplacesrf.data.remote.model.PlaceDto
import retrofit2.Retrofit

class PlaceRepository(

    private val retrofit: Retrofit

)
{
    // instancia al API para acceder a endpoints
    private val placeApi=  retrofit.create(PlacesApi::class.java)

    suspend fun getPlaces(url: String?): List<PlaceDto> = placeApi.getPlaces(url)

    suspend fun getPlaces(): List<PlaceDto> = placeApi.getPlaces()

    suspend fun getPlaceDetail(id: String?): PlaceDetailDto = placeApi.getPlaceDetail(id)

    suspend fun getPlacesApiary(): List<PlaceDto> = placeApi.getPlacesApiary()

    suspend fun getPlaceDetailApiary(id: String): PlaceDetailDto = placeApi.getPlaceDetailApiary(id)

}