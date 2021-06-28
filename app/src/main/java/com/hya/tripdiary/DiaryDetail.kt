package com.hya.tripdiary

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.hya.tripdiary.adapters.DiaryAdapter
import com.hya.tripdiary.database.AppDatabase
import com.hya.tripdiary.database.Diary
import kotlinx.android.synthetic.main.activity_diary_detail.*
import java.io.File

class DiaryDetail : AppCompatActivity() {
    private var diaryData = listOf<Diary>()
    private var appDatabase: AppDatabase? = null
    var selectedDate:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_detail)

        val id = intent.getStringExtra("id")
        selectedDate = intent.getStringExtra("selectedDate")

        refreshList(id.toInt())

        detail_Edit_btn.setOnClickListener {
            val intent = Intent(this, DiaryEdit::class.java)
            intent.putExtra("id", id)
            intent.putExtra("SelectedDay", selectedDate)
            startActivity(intent)
        }

        detail_Delete_btn.setOnClickListener {
            appDatabase = AppDatabase.getInstance(this)

            val r = Runnable {
                try {
                    appDatabase?.diaryDao()?.deleteDetail(selectedId = id.toInt())!!

                    val filePath = Environment.getExternalStorageDirectory().toString() + "/TripDiary/" + selectedDate + "/" + id + ".jpg"
                    val file = File(filePath)
                    if(file.exists()) {
                        file.delete()
                    }

                } catch (e: Exception) {
                    Log.e("tag", "Error - $e")
                }
            }

            val thread = Thread(r)
            thread.start()

            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        val id = intent.getStringExtra("id")
        refreshList(id.toInt())
    }

    private fun refreshList(id: Int) {

        appDatabase = AppDatabase.getInstance(this)

        val r = Runnable {
            try {
                diaryData = appDatabase?.diaryDao()?.getDetail(selectedId = id)!!

                val title = diaryData[0].title.toString()
                val time = diaryData[0].time.toString()
                val contents = diaryData[0].contents.toString()

                detail_Title.setText(title)
                detail_Content.setText(contents)

                getImage(time, id.toString())

            } catch (e: Exception) {
                Log.e("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()
    }

    fun getImage(time: String, id: String) {
        val bitmap = BitmapFactory.decodeFile(
            Environment.getExternalStorageDirectory().toString() + "/TripDiary/" + time + "/" + id.toString() + ".jpg")
        Log.e("path : ", Environment.getExternalStorageDirectory().toString() + "/TripDiary/" + time + "/" + id.toString() + ".jpg")
        detail_image.setImageBitmap(bitmap)
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        appDatabase = null
        super.onDestroy()
    }
}
