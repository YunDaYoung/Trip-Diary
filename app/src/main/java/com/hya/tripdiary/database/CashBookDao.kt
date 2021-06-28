package com.hya.tripdiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface CashBookDao {
    @Query("SELECT * FROM cashbooks WHERE time = :seletedTime")
    fun getAll(seletedTime :String): List<CashBook>

    @Insert(onConflict = REPLACE)
    fun insert(cashBook: CashBook)

    @Query("DELETE from cashbooks")
    fun deleteALL()

    @Query("DELETE from cashbooks WHERE id = :seletedId")
    fun deleteData(seletedId : Int)
}