package com.hya.tripdiary.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hya.tripdiary.CashBookList
import com.hya.tripdiary.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.fragment_cashbook_cal.*
import org.threeten.bp.format.DateTimeFormatter

class CashCalendar : Fragment(), OnDateSelectedListener {
    private lateinit var widget: MaterialCalendarView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cashbook_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        widget = view.findViewById(R.id.cashCalendar)

        widget.setOnDateChangedListener(this)
        widget.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
    }



    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        val nextIntent = Intent(activity, CashBookList::class.java)
        nextIntent.putExtra("SelectedDay", date.toString().slice(IntRange(12, date.toString().length-2)))
        startActivity(nextIntent)
    }
}