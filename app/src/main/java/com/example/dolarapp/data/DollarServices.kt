package com.example.dolarapp.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface DollarServices {
    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("dolares")
    suspend fun getDollar(): Response<MutableList<DollarModel>>
}