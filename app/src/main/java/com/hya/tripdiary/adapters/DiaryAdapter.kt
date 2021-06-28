package com.hya.tripdiary.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.hya.tripdiary.DiaryDetail
import com.hya.tripdiary.R
import com.hya.tripdiary.database.Diary

class DiaryAdapter (val context: Context, val diarys: List<Diary>): RecyclerView.Adapter<DiaryAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_diary_list_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
            return diarys.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(diarys[position])

        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            val context = v.context
            val intent = Intent(context,  DiaryDetail::class.java)
            intent.putExtra("id", diarys[position].id.toString())
            intent.putExtra("selectedDate", diarys[position].time.toString())
            context.startActivity(intent)
        })
    }

    inner class Holder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        val titleTv = itemView?.findViewById<TextView>(R.id.diaryListTitle)

        fun bind(diary: Diary) {
            titleTv?.text = diary.title
        }
    }
}