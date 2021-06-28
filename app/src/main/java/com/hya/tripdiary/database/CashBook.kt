package com.hya.tripdiary.database

import androidx.room.*

@Entity(tableName = "cashbooks")
class CashBook(@PrimaryKey(autoGenerate = true) var id: Int?,
               @ColumnInfo(name = "statement") var statement: String?,
               @ColumnInfo(name = "itemize") var itemize: String?,
               @ColumnInfo(name = "amounts") var amounts: String?,
               @ColumnInfo(name = "note") var note: String?,
               @ColumnInfo(name = "time") var time: String?
)
