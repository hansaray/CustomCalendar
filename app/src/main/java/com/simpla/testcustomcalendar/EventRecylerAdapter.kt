package com.simpla.testcustomcalendar

import android.content.Context

import androidx.annotation.NonNull

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup


class EventRecyclerAdapter(context: Context, arrayList: ArrayList<Events>) :
    RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder>() {
    var context: Context
    var arrayList: ArrayList<Events>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.event_rowlayout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val events = arrayList[position]
        holder.Event.text = events.event
        holder.Time.text = events.time
        holder.Date.text = events.date
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Event: TextView
        var Date: TextView
        var Time: TextView

        init {
            Event = itemView.findViewById(R.id.eventname)
            Date = itemView.findViewById(R.id.eventdate)
            Time = itemView.findViewById(R.id.eventtime)
        }
    }

    init {
        this.context = context
        this.arrayList = arrayList
    }
}