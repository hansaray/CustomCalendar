package com.simpla.testcustomcalendar

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase

import android.view.LayoutInflater

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.util.AttributeSet
import android.util.Log
import android.view.ScrollCaptureCallback
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener

import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior


class CalendarCustomView : LinearLayout {
    var PreviouseButton: ImageButton? = null
    var NextButton: ImageButton? = null
    var CurrentDate: TextView? = null
    var gridView: GridView? = null
    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    var monthFormat: SimpleDateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
    var yearFormat: SimpleDateFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)
    var calendar: Calendar = Calendar.getInstance(Locale.ENGLISH)
    var mContext: Context? = null
    var eventsList: MutableList<Events> = ArrayList()
    var dateList: MutableList<Date> = ArrayList()
    var dbOpenHelper: DBOpenHelper? = null
    var alertDialog: AlertDialog? = null
    var adapter: MyGridAdapter? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
        IntializeUILayout()
        SetupCalendar()
        PreviouseButton!!.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            SetupCalendar()
        }
        NextButton!!.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            SetupCalendar()
        }
        gridView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                builder.setCancelable(true)
                val eventView: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.new_event_layout, null)
                val EventBody: EditText = eventView.findViewById(R.id.eventname)
                val EventTime: TextView = eventView.findViewById(R.id.eventtime)
                val SelectTime: Button = eventView.findViewById(R.id.seteventtime)
                val AddEvent: Button = eventView.findViewById(R.id.addevent)
                SelectTime.setOnClickListener(OnClickListener {
                    val calendar: Calendar = Calendar.getInstance()
                    val hours: Int = calendar.get(Calendar.HOUR_OF_DAY)
                    val minuts: Int = calendar.get(Calendar.MINUTE)
                    val timePickerDialog: TimePickerDialog
                    timePickerDialog = TimePickerDialog(getContext(), R.style.ThemeOverlay_Material3_Dialog,
                        { view, hourOfDay, minute ->
                            val c: Calendar = Calendar.getInstance()
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            c.set(Calendar.MINUTE, minute)
                            c.setTimeZone(TimeZone.getDefault())
                            val format = SimpleDateFormat("K:mm a", Locale.ENGLISH)
                            val PlannedTime: String = format.format(c.getTime())
                            EventTime.text = PlannedTime
                        }, hours, minuts, false
                    )
                    timePickerDialog.show()
                })
                val date: String = dateFormat.format(dateList[position])
                AddEvent.setOnClickListener(OnClickListener {
                    SaveEvent(
                        EventBody.text.toString(),
                        EventTime.text.toString(),
                        date,
                        monthFormat.format(dateList[position]),
                        yearFormat.format(dateList[position])
                    )
                    SetupCalendar()
                    alertDialog?.dismiss()
                })
                builder.setView(eventView)
                alertDialog = builder.create()
                alertDialog?.show()
            }
        gridView!!.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id ->
                val date: String = dateFormat.format(dateList[position])
                val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                builder.setCancelable(true)
                val showView: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.show_events_layout, null)
                val EventRV = showView.findViewById(R.id.EventsRV) as RecyclerView
                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(showView.getContext())
                EventRV.layoutManager = layoutManager
                EventRV.setHasFixedSize(true)
                val eventRecyclerAdapter =
                    EventRecyclerAdapter(showView.getContext(), CollectEvent(date))
                EventRV.adapter = eventRecyclerAdapter
                eventRecyclerAdapter.notifyDataSetChanged()
                builder.setView(showView)
                alertDialog = builder.create()
                alertDialog?.show()
                true
            }
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @SuppressLint("Range")
    private fun CollectEvent(date: String): ArrayList<Events> {
        val arrayList: ArrayList<Events> = ArrayList()
        dbOpenHelper = DBOpenHelper(context)
        val sqLiteDatabase: SQLiteDatabase? = dbOpenHelper?.readableDatabase
        val cursor: Cursor? = dbOpenHelper?.ReadEvent(date, sqLiteDatabase!!)
        while (cursor?.moveToNext() == true) {
            val event: String = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT))
            val Time: String = cursor.getString(cursor.getColumnIndex(DBStructure.TIME))
            val Date: String = cursor.getString(cursor.getColumnIndex(DBStructure.DATE))
            val month: String = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH))
            val year: String = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR))
            val events = Events(event, Time, Date, month, year)
            arrayList.add(events)
        }
        cursor?.close()
        dbOpenHelper?.close()
        // Toast.makeText(context, String.valueOf(arrayList.size()), Toast.LENGTH_SHORT).show();
        return arrayList
    }

    private fun IntializeUILayout() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.calendar_layout, this)
        PreviouseButton = view.findViewById(R.id.previousBtn)
        NextButton = view.findViewById(R.id.nextBtn)
        CurrentDate = view.findViewById(R.id.current_Date)
        gridView = view.findViewById(R.id.gridview)

        var mainLayout : LinearLayout? = null
        mainLayout = view.findViewById(R.id.main_layout)
        var coordinator : CoordinatorLayout? = null
        coordinator = view.findViewById(R.id.coordinator)
        var nested : NestedScrollView? = null
        nested = view.findViewById(R.id.nested_scroll)
        var collapsing : CollapsingToolbarLayout? = null
        collapsing = view.findViewById(R.id.collapsing_layout)
        val params = collapsing.layoutParams as AppBarLayout.LayoutParams

        /*mainLayout.setOnScrollChangeListener(OnScrollChangeListener { view, i, i1, i2, i3 ->
            Log.e("CE main","girdi")
            if(i == 100){
                Log.e("CE main2","girdi")
               // params.scrollFlags = 0
                //collapsing.layoutParams = params
            }/*else{
                params.scrollFlags =
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                collapsing.layoutParams = params
            }*/

        })*/

        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        collapsing.layoutParams = params

        val bottomSheetBehavior = BottomSheetBehavior.from(nested)

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Do something for new state
                Log.e("CE state",newState.toString())
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset
                Log.e("CE nested1",slideOffset.toString())
                if(slideOffset.toDouble() >= 0.450){
                    Log.e("CE nested21","girdi")
                    params.scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    collapsing.layoutParams = params
                }else if (slideOffset <= 0.450){
                    /*params.scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    collapsing.layoutParams = params*/
                }
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

    }

    private fun SetupCalendar() {
        val StartDate: String = simpleDateFormat.format(calendar.getTime())
        CurrentDate!!.text = StartDate
        dateList.clear()
        val monthCalendar: Calendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val FirstDayOfMonth: Int = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth)
        COllectEventsPerMonth(
            monthFormat.format(calendar.getTime()),
            yearFormat.format(calendar.getTime())
        )
        while (dateList.size < MAX_CALENDAR_Days) {
            dateList.add(monthCalendar.getTime())
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        adapter = MyGridAdapter(context, dateList, calendar, eventsList)
        gridView!!.adapter = adapter
    }

    private fun SaveEvent(event: String, time: String, date: String, Month: String, Year: String) {
        dbOpenHelper = DBOpenHelper(context)
        val database = dbOpenHelper!!.writableDatabase
        dbOpenHelper?.SaveEvent(event, time, date, Month, Year,null, database)
        dbOpenHelper?.close()
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show()
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

    @SuppressLint("Range")
    private fun COllectEventsPerMonth(Month: String, Year: String) {
        eventsList.clear()
        dbOpenHelper = DBOpenHelper(context)
        val database: SQLiteDatabase = dbOpenHelper!!.getReadableDatabase()
        val cursor: Cursor = dbOpenHelper!!.ReadEventperMonth(Month, Year, database)
        while (cursor.moveToNext()) {
            val event: String = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT))
            val Time: String = cursor.getString(cursor.getColumnIndex(DBStructure.TIME))
            val Date: String = cursor.getString(cursor.getColumnIndex(DBStructure.DATE))
            val month: String = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH))
            val year: String = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR))
            val events = Events(event, Time, Date, month, year)
            eventsList.add(events)
        }
    }

    companion object {
        private const val MAX_CALENDAR_Days = 42
    }
}