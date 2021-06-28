package com.hya.tripdiary.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maps")
class Map (@PrimaryKey(autoGenerate = true) var id: Int?,
           @ColumnInfo(name = "latitude") var latitude: Double?,
           @ColumnInfo(name = "longitude") var longitude: Double
)