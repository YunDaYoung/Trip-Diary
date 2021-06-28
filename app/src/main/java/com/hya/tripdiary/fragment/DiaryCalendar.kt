package com.hya.tripdiary.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hya.tripdiary.DiaryList
import com.hya.tripdiary.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

class DiaryCalendar : Fragment(), OnDateSelectedListener {
    private lateinit var widget: MaterialCalendarView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_diary_cal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        widget = view.findViewById(R.id.diaryCalendar)

        widget.setOnDateChangedListener(this)
        widget.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE

    }

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        val nextIntent = Intent(activity, DiaryList::class.java)
        nextIntent.putExtra("SelectedDay", date.toString().slice(IntRange(12, date.toString().length-2)))
        startActivity(nextIntent)
    }
}