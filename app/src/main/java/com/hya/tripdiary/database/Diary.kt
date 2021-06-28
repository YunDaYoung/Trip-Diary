package com.hya.tripdiary.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diarys")
class Diary(@PrimaryKey(autoGenerate = true) var id: Int?,
            @ColumnInfo(name = "title") var title: String?,
            @ColumnInfo(name = "contents") var contents: String?,
            @ColumnInfo(name = "time") var time: String?
)