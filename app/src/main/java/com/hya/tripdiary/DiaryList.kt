package com.hya.tripdiary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hya.tripdiary.adapters.DiaryAdapter
import com.hya.tripdiary.database.AppDatabase
import com.hya.tripdiary.database.Diary
import kotlinx.android.synthetic.main.activity_diary_list.*

class DiaryList : AppCompatActivity() {
    private var diaryList = listOf<Diary>()
    private var appDatabase: AppDatabase? = null
    lateinit var mAdapter: DiaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        refreshList()
    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }

    private fun refreshList() {
        //Checking the selected date
        if(intent.hasExtra("SelectedDay")){
            //Getting selected date
            val selectedDate = intent.getStringExtra("SelectedDay")
            Log.e("selectedDate", selectedDate)

            selectedDateText.text = selectedDate

            diaryFab.setOnClickListener {
                val nextIntent = Intent(this, DiaryRegister::class.java)
                nextIntent.putExtra("SelectedDay", selectedDate)
                startActivity(nextIntent)
            }

            appDatabase = AppDatabase.getInstance(this)

            val r = Runnable {
                try {
                    diaryList = appDatabase?.diaryDao()?.getAll(seletedTime = selectedDate)!!
                    mAdapter = DiaryAdapter(
                        this,
                        diaryList
                    )
                    mAdapter.notifyDataSetChanged()

                    diaryRecyclerView.adapter = mAdapter
                    diaryRecyclerView.layoutManager = LinearLayoutManager(this)
                    diaryRecyclerView.setHasFixedSize(true)
                } catch (e: Exception) {
                    Log.d("tag", "Error - $e")
                }
            }

            val thread = Thread(r)
            thread.start()
        }
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        appDatabase = null
        super.onDestroy()
    }
}
