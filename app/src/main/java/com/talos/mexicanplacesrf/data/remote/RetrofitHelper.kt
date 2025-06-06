package com.talos.mexicanplacesrf.data.remote

import com.talos.mexicanplacesrf.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ayuda de instacionación retrofit
class RetrofitHelper {

    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // nivel body en operacion de red
    }

    val client = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
    }.build()

    fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // ruta Apiary en constantes
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())// convertidor de deserialización
            .build()
}
