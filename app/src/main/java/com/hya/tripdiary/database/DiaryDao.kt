package com.hya.tripdiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diarys WHERE time = :seletedTime")
    fun getAll(seletedTime :String): List<Diary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: Diary)

    @Query("DELETE from diarys")
    fun deleteALL()

    @Query("SELECT MAX(id) from diarys")
    fun getMaxId(): Int

    @Query("SELECT * from diarys WHERE id = :selectedId")
    fun getDetail(selectedId: Int): List<Diary>

    @Query("DELETE from diarys WHERE id = :selectedId")
    fun deleteDetail(selectedId: Int)

    @Query("UPDATE diarys set title = :title, contents = :content WHERE id = :selectedId")
    fun updateDetail(selectedId: Int, title: String, content:String)
}