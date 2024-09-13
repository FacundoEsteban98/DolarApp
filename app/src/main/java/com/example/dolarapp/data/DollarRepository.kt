package com.example.dolarapp.data

import retrofit2.Response
import javax.inject.Inject

class DollarRepository@Inject constructor(
    private val apiService: DollarServices
) {
    suspend fun getDollar(): Response<MutableList<DollarModel>> {
        return apiService.getDollar()
    }
}