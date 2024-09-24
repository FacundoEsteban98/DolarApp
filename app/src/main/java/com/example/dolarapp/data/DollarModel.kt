package com.example.dolarapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "dollar")
data class DollarEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "moneda") @SerializedName("moneda") val currency: String,
    @ColumnInfo(name = "casa") @SerializedName("casa") val house: String,
    @ColumnInfo(name = "nombre") @SerializedName("nombre") val name: String,
    @ColumnInfo(name = "compra") @SerializedName("compra") val purchase: Double,
    @ColumnInfo(name = "venta") @SerializedName("venta") val sale: Double,
    @ColumnInfo(name = "fechaActualizacion") @SerializedName("fechaActualizacion") val updateDate: String
)

data class DollarSummary(
    @ColumnInfo(name = "moneda") val currency: String,
    @ColumnInfo(name = "casa") val house: String,
    @ColumnInfo(name = "nombre") val name: String,
    @ColumnInfo(name = "compra") val purchase: Double,
    @ColumnInfo(name = "venta") val sale: Double,
    @ColumnInfo(name = "fechaActualizacion") val updateDate: String
)
