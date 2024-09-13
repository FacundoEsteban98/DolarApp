package com.example.dolarapp.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DollarModel(
    @SerializedName("moneda")
    val currency: String,
    @SerializedName("casa")
    val house: String,
    @SerializedName("nombre")
    val name: String,
    @SerializedName("compra")
    val purchase: Double,
    @SerializedName("venta")
    val sale: Double,
    @SerializedName("fechaActualizacion")
    val updateDate: String
)