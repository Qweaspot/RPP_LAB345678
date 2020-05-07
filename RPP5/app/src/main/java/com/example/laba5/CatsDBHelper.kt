package com.example.laba5

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val DATABASE_NAME = "CatsDB"
private const val DATABASE_VERSION = 2

object CatRecord : BaseColumns{
    const val CAT_TABLE = "Cat"
    const val ID_COLUMN = BaseColumns._ID
    const val CAT_ID_COLUMN = "cat_id_api"
    const val IMAGE_URL_COLUMN = "image_url"
}

private const val SQL_CREATE_RECORDS =
    "CREATE TABLE IF NOT EXISTS ${CatRecord.CAT_TABLE}(" +
            "${CatRecord.ID_COLUMN} INTEGER PRIMARY KEY," +
            "${CatRecord.CAT_ID_COLUMN} TEXT," +
            "${CatRecord.IMAGE_URL_COLUMN} TEXT);"

private const val SQL_DELETE_RECORDS =
    "DELETE FROM ${CatRecord.CAT_TABLE}"

private const val SQL_DELETE_TABLE =
    "DROP TABLE IF EXISTS ${CatRecord.CAT_TABLE}"

class CatsDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_RECORDS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion){
            db?.execSQL(SQL_DELETE_TABLE)
            onCreate(db)
        }
    }

    fun truncate(db: SQLiteDatabase?){
        db?.execSQL(SQL_DELETE_RECORDS)
    }
}