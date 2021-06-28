package com.hya.tripdiary.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hya.tripdiary.R
import com.hya.tripdiary.database.AppDatabase
import com.hya.tripdiary.database.CashBook

class CashBookAdapter (val context: Context, val cashbooks: List<CashBook>): RecyclerView.Adapter<CashBookAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_cashbook_list_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return cashbooks.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(cashbooks[position])

        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            val context = v.context
            val dlg = AlertDialog.Builder(context)
            dlg.setTitle("Delete notification.")
            dlg.setMessage("Are you sure you want to delete this data?")
            dlg.setIcon(R.mipmap.ic_launcher)
            dlg.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                deleteData(cashbooks[position].id.toString())
            })
            dlg.setNegativeButton("Cancel", null)
            dlg.show()
        })
    }

    inner class Holder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        val statementTv = itemView?.findViewById<TextView>(R.id.cashBookListStatement)
        val itemizeTv = itemView?.findViewById<TextView>(R.id.cashBookListItemize)
        val amountsTv = itemView?.findViewById<TextView>(R.id.cashBookListAmounts)

        fun bind(cashBook: CashBook) {
            statementTv?.text = cashBook.statement
            itemizeTv?.text = cashBook.itemize
            amountsTv?.text = cashBook.amounts
        }
    }

    private var appDatabase: AppDatabase? = null
    fun deleteData(id : String) {
        appDatabase = AppDatabase.getInstance(context)

        var r = Runnable {
            try {
                appDatabase?.cashBookDao()?.deleteData(seletedId = id.toInt())!!
            } catch (e : Exception) {
                Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()
        (context as Activity).recreate()
    }
}