package com.simpla.testcustomcalendar

import android.content.Context
import android.graphics.Color
import android.graphics.Color.green
import android.provider.CalendarContract

import android.widget.TextView

import android.view.ViewGroup

import androidx.annotation.NonNull

import android.view.LayoutInflater

import android.view.View

import android.widget.ArrayAdapter
import androidx.annotation.Nullable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyGridAdapter(
    context: Context?,
    dates: List<Date?>,
    currentDate: Calendar,
    eventsList: MutableList<Events>
) :
    ArrayAdapter<Any?>(context!!, R.layout.single_cell_layout) {
    var dates: List<Date?>
    var currentDate: Calendar
    var inflater: LayoutInflater
    var eventsList: MutableList<Events>

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val monthDate: Date? = dates[position]
        val dateCalendar: Calendar = Calendar.getInstance()
        dateCalendar.setTime(monthDate)
        val dayNo: Int = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val displayMonth: Int = dateCalendar.get(Calendar.MONTH) + 1
        val displayYear: Int = dateCalendar.get(Calendar.YEAR)
        val currentYear: Int = currentDate.get(Calendar.YEAR)
        val currentMonth: Int = currentDate.get(Calendar.MONTH) + 1
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, parent, false)
        }
        if (displayMonth == currentMonth && displayYear == currentYear) {
            view?.setBackgroundColor(context.resources.getColor(R.color.green))
        } else {
            view?.setBackgroundColor(Color.parseColor("#cccccc"))
        }
        val cellNumber: TextView = view?.findViewById(R.id.calendar_day)!!
        val eventText: TextView = view.findViewById(R.id.events_id)!!
        cellNumber.text = dayNo.toString()
        val eventCalendar: Calendar = Calendar.getInstance()
        val arrayList: ArrayList<String> = ArrayList()
        for (i in eventsList.indices) {
            eventCalendar.setTime(convertStringToDate(eventsList[i].date))
            if (dayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(
                    Calendar.MONTH
                ) + 1 && displayYear == eventCalendar.get(Calendar.YEAR)
            ) {
                arrayList.add(eventsList[i].event)
                eventText.setText(arrayList.size.toString() + " events")
            }
        }
        return view
    }

    override fun getCount(): Int {
        return dates.size
    }

    @Nullable
    override fun getItem(position: Int): Any? {
        return dates[position]
    }

    override fun getPosition(@Nullable item: Any?): Int {
        return dates.indexOf(item)
    }

    private fun convertStringToDate(dateInString: String): Date? {
        val format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = format.parse(dateInString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    init {
        this.dates = dates
        this.currentDate = currentDate
        inflater = LayoutInflater.from(context)
        this.eventsList = eventsList
    }
}