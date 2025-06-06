package com.talos.mexicanplacesrf.data.remote

import com.talos.mexicanplacesrf.data.remote.model.PlaceDetailDto
import com.talos.mexicanplacesrf.data.remote.model.PlaceDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

//Analogo en el Dao
interface PlacesApi {

    //    https://www.serverbpw.com/cm/games/games_list.php
    //    https://www.serverbpw.com/cm/games/game_detail.php?id=21357
    //
    //    https://private-a649a-games28.apiary-mock.com/games/games_list
    //    https://private-a649a-games28.apiary-mock.com/games/game_detail/21357

    //    https://private-d88be0-pueblos.apiary-mock.com/pueblos/pueblos_list
    //    https://private-d88be0-pueblos.apiary-mock.com/pueblos/pueblo_detail/0001

    // entiende la peticion
    @GET
    suspend fun getPlaces(
        @Url // agrega lo faltante de la ruta
        url: String?
    ): List<PlaceDto>

    // hace lo mismo de otra forma
    @GET("cm/games/games_list.php")
    suspend fun getPlaces(): List<PlaceDto>

    @GET("cm/games/game_detail.php")
    suspend fun getPlaceDetail(
        @Query("id")// arma igual que la url desde un servidor propio
        id: String?/*,
        @Query("name")
        name: String?*/
    ): PlaceDetailDto

    @GET("pueblos/pueblos_list")
    suspend fun getPlacesApiary(): List<PlaceDto>

    @GET("pueblos/pueblo_detail/{id}")  // siguente valor con diagonal llaves
    suspend fun getPlaceDetailApiary(
        @Path("id")
        id: String?/*,
        @Path("id")
        name: String?*/
    ): PlaceDetailDto
}