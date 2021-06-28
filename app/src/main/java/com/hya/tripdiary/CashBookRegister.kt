package com.hya.tripdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.ToggleButton
import com.hya.tripdiary.database.CashBook
import com.hya.tripdiary.database.AppDatabase
import kotlinx.android.synthetic.main.activity_cashbook_register.*

class CashBookRegister : AppCompatActivity() {

    var selectedDateData: String? = null
    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashbook_register)

        appDatabase = AppDatabase.getInstance(this)
        selectedDateData = intent.getStringExtra("SelectedDay")

        var statement: String? = "INCOME"
        val toggle: ToggleButton = findViewById(R.id.statementBtn)
        toggle.setOnClickListener {
            if(toggle.isChecked) {
                statement = "OUTGOING"
            } else {
                statement = "INCOME"
            }
        }

        val addRunnable = Runnable {
            val newCashBook = CashBook(
                id = null,
                statement = statement,
                itemize = addItemize.text.toString(),
                amounts = addAmounts.text.toString() + " €",
                note = addNote.text.toString(),
                time = selectedDateData
            )
            appDatabase?.cashBookDao()?.insert(newCashBook)
        }

        cancelBtn.setOnClickListener {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            finish()
        }

        selectedDateText.text = selectedDateData

        saveBtn.setOnClickListener {
            if (addItemize.text.toString().isBlank()) {
                Toast.makeText(this, "Please add itemize", Toast.LENGTH_SHORT).show()
            } else if (addAmounts.text.toString().isBlank()) {
                Toast.makeText(this, "Please add amounts", Toast.LENGTH_SHORT).show()
            } else if (addNote.text.toString().isBlank()) {
                Toast.makeText(this, "Please add note", Toast.LENGTH_SHORT).show()
            } else {
                val addThread = Thread(addRunnable)
                addThread.start()

                finish()
            }
        }
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }
}
