package com.example.lab6

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class SplashScreen : AppCompatActivity() {

    private var list: ArrayList<Item>? = null
    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null
    internal val LOG_TAG = "myLogs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)
        list = ArrayList()
        dbHelper = DBHelper(this@SplashScreen)
        if (savedInstanceState == null)
            PrefetchData().execute()
    }

    private inner class PrefetchData : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg arg0: Void): Void? {
            database = dbHelper!!.writableDatabase
            val c = database!!.query(DBHelper.TABLE_PRODUCTS, null, null, null, null, null, null)
            if (c.moveToFirst()) {
                val idColIndex = c.getColumnIndex(DBHelper.KEY_ID)
                val nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME)
                val priceColIndex = c.getColumnIndex(DBHelper.KEY_PRICE)
                val quantityColIndex = c.getColumnIndex(DBHelper.KEY_QUANTITY)
                do {
                    list!!.add(
                        Item(
                            c.getInt(idColIndex),
                            c.getString(nameColIndex),
                            c.getFloat(priceColIndex),
                            c.getInt(quantityColIndex)
                        )
                    )
                } while (c.moveToNext())
            }
            c.close()
            database!!.close()
            return null
        }

        override fun onPostExecute(result: Void) {
            super.onPostExecute(result)
            val i = Intent(this@SplashScreen, StoreFrontActivity::class.java)
            i.putParcelableArrayListExtra("listOfItems", list)
            startActivity(i)
            finish()
        }
    }


}
