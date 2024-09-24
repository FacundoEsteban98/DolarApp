package com.example.dolarapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dolarapp.data.DollarEntity

@Database(entities = [DollarEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dollarDao(): DollarDao
}