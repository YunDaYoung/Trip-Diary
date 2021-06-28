package com.hya.tripdiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.hya.tripdiary.database.CashBook
import com.hya.tripdiary.adapters.CashBookAdapter
import com.hya.tripdiary.database.AppDatabase
import kotlinx.android.synthetic.main.activity_cashbook_list.*
import java.lang.Exception

class CashBookList : AppCompatActivity() {
    private var cashList = listOf<CashBook>()
    private var appDatabase: AppDatabase? = null
    lateinit var mAdapter: CashBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashbook_list)

        refreshList()
    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }

    private fun refreshList() {
        //Checking the selected date
        if (intent.hasExtra("SelectedDay")) {
            //Getting selected date
            val selectedDate = intent.getStringExtra("SelectedDay")
            Log.e("selectedDate", selectedDate)

            selectedDateText.text = selectedDate

            cashFab.setOnClickListener {
                val nextIntent = Intent(this, CashBookRegister::class.java)
                nextIntent.putExtra("SelectedDay", selectedDate)
                startActivity(nextIntent)
            }

            appDatabase = AppDatabase.getInstance(this)

            val r = Runnable {
                try {
                    cashList = appDatabase?.cashBookDao()?.getAll(seletedTime = selectedDate)!!
                    mAdapter = CashBookAdapter(
                        this,
                        cashList
                    )
                    mAdapter.notifyDataSetChanged()

                    cashRecyclerView.adapter = mAdapter
                    cashRecyclerView.layoutManager = LinearLayoutManager(this)
                    cashRecyclerView.setHasFixedSize(true)
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
