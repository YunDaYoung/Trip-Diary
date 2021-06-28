package com.hya.tripdiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MapDao {
    @Query("SELECT * FROM maps")
    fun getAll(): List<Map>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(map: Map)

    @Query("DELETE from maps")
    fun deleteALL()

    @Query("SELECT MAX(id) from maps")
    fun getMaxId(): Int

    @Query("DELETE from maps WHERE id = :selectedId")
    fun deleteRecently(selectedId: Int)
}