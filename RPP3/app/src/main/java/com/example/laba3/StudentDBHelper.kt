package com.example.laba3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val DATABASE_NAME = "StudentsDB"
private const val DATABASE_VERSION = 1

object StudentRecord : BaseColumns{
    const val STUDENT_TABLE = "Student"
    const val FIO_COLUMN = "FIO"
    const val DATE_ADD_COLUMN = "DateOfAdd"
}

private const val SQL_CREATE_RECORDS =
    "CREATE TABLE IF NOT EXISTS ${StudentRecord.STUDENT_TABLE} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${StudentRecord.FIO_COLUMN} TEXT," +
            "${StudentRecord.DATE_ADD_COLUMN} TEXT);"

private const val SQL_DELETE_TABLE =
    "DROP TABLE IF EXISTS ${StudentRecord.STUDENT_TABLE}"

private const val SQL_TRUNCATE_RECORDS =
    "DELETE FROM ${StudentRecord.STUDENT_TABLE}"


class StudentDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_RECORDS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db?.execSQL(SQL_DELETE_TABLE)
            onCreate(db)
        }
    }

    fun truncate(db: SQLiteDatabase?){
        db?.execSQL(SQL_TRUNCATE_RECORDS)
    }

}