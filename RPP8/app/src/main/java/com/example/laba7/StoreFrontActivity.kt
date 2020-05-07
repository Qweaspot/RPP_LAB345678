package com.example.laba7

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
        dbHelper = DBHelper(this@StoreFrontActivity)
        if (i.hasExtra("listOfItems")) {
            list = i.getParcelableArrayListExtra("listOfItems")
        } else {
            database = dbHelper!!.writableDatabase
            val c = database!!.rawQuery(query, null)
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
        }
        addList()

        handler1 = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                list = bundle.getParcelableArrayList("listOfProducts")
                val it = bundle.getParcelable<Item>("item")
                val toast1 = Toast.makeText(
                    this@StoreFrontActivity, "Товар \"" + it!!.name
                            + "\" изменен", Toast.LENGTH_SHORT
                )
                toast1.setGravity(
                    Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                    0,
                    200
                )
                toast1.show()
                recyclerView!!.adapter =
                    AdapterRecyclerView(this@StoreFrontActivity, list, this@StoreFrontActivity)
                recyclerView!!.adapter!!.notifyDataSetChanged()
                if (list!!.size > 0) {
                    textView!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                }
                if (list!!.size == 0) {
                    val toast =
                        Toast.makeText(this@StoreFrontActivity, "Товаров нет", Toast.LENGTH_SHORT)
                    toast.setGravity(
                        Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                        0,
                        200
                    )
                    toast.show()
                    recyclerView!!.visibility = View.GONE
                    textView!!.visibility = View.VISIBLE
                }
            }
        }


        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                list = bundle.getParcelableArrayList("listOfProducts")
                val it = bundle.getParcelable<Item>("item")
                val toast1 = Toast.makeText(
                    this@StoreFrontActivity, "Товар \"" + it!!.name
                            + "\" куплен", Toast.LENGTH_SHORT
                )
                toast1.setGravity(
                    Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                    0,
                    200
                )
                toast1.show()
                recyclerView!!.adapter =
                    AdapterRecyclerView(this@StoreFrontActivity, list, this@StoreFrontActivity)
                recyclerView!!.adapter!!.notifyDataSetChanged()
                if (list!!.size > 0) {
                    textView!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                }
                if (list!!.size == 0) {
                    val toast =
                        Toast.makeText(this@StoreFrontActivity, "Товаров нет", Toast.LENGTH_SHORT)
                    toast.setGravity(
                        Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                        0,
                        200
                    )
                    toast.show()
                    recyclerView!!.visibility = View.GONE
                    textView!!.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun addList() {
        if (list!!.size > 0) {
            adapterRecyclerView = AdapterRecyclerView(this@StoreFrontActivity, list, this)
            textView!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
            recyclerView!!.adapter = adapterRecyclerView
        } else {
            recyclerView!!.visibility = View.GONE
            textView!!.visibility = View.VISIBLE
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

    companion object {
        var handler: Handler
        var handler1: Handler
        var query =
            "SELECT * FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.KEY_QUANTITY + "<>0;"
    }
}
