package com.simpla.testcustomcalendar

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable


class DBOpenHelper(@Nullable context: Context?) :
    SQLiteOpenHelper(context, DBStructure.DB_NAME, null, DBStructure.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_EVENTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_EVENTS_TABLE)
        onCreate(db)
    }

    fun SaveEvent(
        event: String?,
        time: String?,
        date: String?,
        month: String?,
        year: String?,
        notify: String?,
        database: SQLiteDatabase
    ) {
        val contentValues = ContentValues()
        contentValues.put(DBStructure.EVENT, event)
        contentValues.put(DBStructure.TIME, time)
        contentValues.put(DBStructure.DATE, date)
        contentValues.put(DBStructure.MONTH, month)
        contentValues.put(DBStructure.YEAR, year)
        contentValues.put(DBStructure.Notify, notify)
        database.insert(DBStructure.EVENT_TABLE_NAME, null, contentValues)
    }

    fun ReadEvent(date: String, database: SQLiteDatabase): Cursor {
        val Projections = arrayOf<String>(
            DBStructure.EVENT,
            DBStructure.TIME,
            DBStructure.DATE,
            DBStructure.MONTH,
            DBStructure.YEAR
        )
        val Selection: String = DBStructure.DATE.toString() + "=?"
        val SelectionArgs = arrayOf(date)
        return database.query(
            DBStructure.EVENT_TABLE_NAME,
            Projections,
            Selection,
            SelectionArgs,
            null,
            null,
            null
        )
    }

    fun ReadIDEvents(date: String, event: String, time: String, database: SQLiteDatabase): Cursor {
        val Projections = arrayOf<String>(DBStructure.ID, DBStructure.Notify, DBStructure.TIME)
        val Selection: String =
            DBStructure.DATE.toString() + "=? and " + DBStructure.EVENT + "=? and " + DBStructure.TIME + "=?"
        val SelectionArgs = arrayOf(date, event, time)
        return database.query(
            DBStructure.EVENT_TABLE_NAME,
            Projections,
            Selection,
            SelectionArgs,
            null,
            null,
            null
        )
    }

    fun ReadEventperMonth(month: String, year: String, database: SQLiteDatabase): Cursor {
        val Projections = arrayOf<String>(
            DBStructure.EVENT,
            DBStructure.TIME,
            DBStructure.DATE,
            DBStructure.MONTH,
            DBStructure.YEAR
        )
        val Selection: String = DBStructure.MONTH.toString() + "=? and " + DBStructure.YEAR + "=?"
        val SelectionArgs = arrayOf(month, year)
        return database.query(
            DBStructure.EVENT_TABLE_NAME,
            Projections,
            Selection,
            SelectionArgs,
            null,
            null,
            null
        )
    }

    fun deleteEvent(event: String, date: String, time: String, database: SQLiteDatabase) {
        val selection: String =
            DBStructure.EVENT.toString() + "=? and " + DBStructure.DATE + "=? and " + DBStructure.TIME + "=?"
        val selectionArg = arrayOf(event, date, time)
        database.delete(DBStructure.EVENT_TABLE_NAME, selection, selectionArg)
    }

    fun updateEvent(
        date: String,
        event: String,
        time: String,
        notify: String?,
        database: SQLiteDatabase
    ) {
        val contentValues = ContentValues()
        contentValues.put(DBStructure.Notify, notify)
        val Selection: String =
            DBStructure.DATE.toString() + "=? and " + DBStructure.EVENT + "=? and " + DBStructure.TIME + "=?"
        val SelectionArgs = arrayOf(date, event, time)
        database.update(DBStructure.EVENT_TABLE_NAME, contentValues, Selection, SelectionArgs)
    }

    companion object {
        private val CREATE_EVENTS_TABLE =
            ("create table " + DBStructure.EVENT_TABLE_NAME.toString() + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBStructure.EVENT.toString() + " TEXT, " + DBStructure.TIME.toString() + " TEXT, " + DBStructure.DATE.toString() + " TEXT, " + DBStructure.MONTH.toString() + " TEXT, "
                    + DBStructure.YEAR.toString() + " TEXT, " + DBStructure.Notify.toString() + " TEXT)")
        private val DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS " + DBStructure.EVENT_TABLE_NAME
    }
}