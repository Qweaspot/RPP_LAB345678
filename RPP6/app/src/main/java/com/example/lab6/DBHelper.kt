package com.example.lab6

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table " + TABLE_PRODUCTS + "(" + KEY_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    KEY_NAME + " TEXT(40), " + KEY_PRICE + " REAL DEFAULT 0.0," + KEY_QUANTITY +
                    " INTEGER(5) DEFAULT 0);"
        )
        println("ТБ создана")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {

        val DATABASE_VERSION = 1
        val DATABASE_NAME = "ProductDB"
        val TABLE_PRODUCTS = "Products"
        val KEY_ID = "_id"
        val KEY_NAME = "Name"
        val KEY_PRICE = "Price"
        val KEY_QUANTITY = "Quantity"
    }
}
