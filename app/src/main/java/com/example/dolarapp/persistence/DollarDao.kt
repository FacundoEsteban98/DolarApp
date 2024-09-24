package com.example.dolarapp.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.dolarapp.data.DollarEntity
import com.example.dolarapp.data.DollarSummary

@Dao
interface DollarDao {
    @Query("SELECT * FROM dollar")
    fun getAll(): List<DollarEntity>

    @Query("SELECT * FROM dollar WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<DollarEntity>

    @Query("SELECT * FROM dollar WHERE nombre LIKE :first LIMIT 1")
    fun findByName(first: String): DollarEntity

    //Como trae todos los de la fecha (indiferente la hora) le asigno un distinct
    //para que me traiga el mas actual respecto la fecha y agrupandolo por casa
    @Query("""
    SELECT DISTINCT casa, fechaActualizacion, venta, compra, moneda, nombre
    FROM dollar 
    WHERE fechaActualizacion IN (
        SELECT MAX(fechaActualizacion)
        FROM dollar 
               WHERE fechaActualizacion LIKE :first 
        GROUP BY casa
    )
""")
    fun findByDate(first: String): List<DollarSummary>

    @Insert
    fun insertAll(vararg dollar: DollarEntity)

    @Delete
    fun delete(dollar: DollarEntity)
}