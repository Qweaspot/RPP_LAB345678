package com.example.lab6

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class StoreFrontActivity : AppCompatActivity(), AdapterRecyclerView.OnNoteListener {
    private var recyclerView: RecyclerView? = null
    private var adapterRecyclerView: AdapterRecyclerView? = null
    private var list: ArrayList<Item>? = null
    private val TAG = "gg"
    private var textView: TextView? = null
    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front)

        recyclerView = findViewById(R.id.recycle_view)
        textView = findViewById(R.id.text_view_products_not)
        val layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(false)
        val i = intent
        list = ArrayList()
        if (i.hasExtra("listOfItems")) {
            list = i.getParcelableArrayListExtra("listOfItems")
            if (list!!.size != 0) {
                adapterRecyclerView = AdapterRecyclerView(this, list!!, this)
                textView!!.visibility = View.GONE
                recyclerView!!.visibility = View.VISIBLE
                recyclerView!!.adapter = adapterRecyclerView
            } else {
                recyclerView!!.visibility = View.GONE
                textView!!.visibility = View.VISIBLE
            }
        } else {
            dbHelper = DBHelper(this@StoreFrontActivity)
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
            if (list!!.size != 0) {
                adapterRecyclerView = AdapterRecyclerView(this, list!!, this)
                textView!!.visibility = View.GONE
                recyclerView!!.visibility = View.VISIBLE
                recyclerView!!.adapter = adapterRecyclerView
            } else {
                recyclerView!!.visibility = View.GONE
                textView!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onNoteClick(position: Int) {
        Log.d(TAG, "clicked")
        val i = Intent(this@StoreFrontActivity, ViewPagerActivity::class.java)
        i.putParcelableArrayListExtra("listOfItems", list)
        i.putExtra("position", position)
        startActivity(i)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_store -> {
                val i = Intent(this@StoreFrontActivity, BackEndActivity::class.java)
                startActivity(i)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
