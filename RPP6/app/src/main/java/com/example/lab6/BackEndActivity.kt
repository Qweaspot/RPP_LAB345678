package com.example.lab6

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.util.ArrayList
import java.util.HashMap

class BackEndActivity : AppCompatActivity(), AdapterRecyclerView.OnNoteListener {
    private var list: ArrayList<Item>? = null
    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null
    private var listProducts: ListView? = null
    private var simpleAdapter: SimpleAdapter? = null
    private var textView: TextView? = null

    private var data: ArrayList<Map<String, Any>>? = null
    private var m: MutableMap<String, Any>? = null

    internal val ATTRIBUTE_ID = "id"
    internal val ATTRIBUTE_NAME = "image"
    internal val LOG_TAG = "myLogs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back)
        textView = findViewById(R.id.text_view_products_not)
        listProducts = findViewById<View>(R.id.productsList) as ListView
        list = ArrayList()
        dbHelper = DBHelper(this@BackEndActivity)

        database = dbHelper!!.writableDatabase
        val c = database!!.query(DBHelper.TABLE_PRODUCTS, null, null, null, null, null, null)
        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex(DBHelper.KEY_ID)
            val nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME)
            val priceColIndex = c.getColumnIndex(DBHelper.KEY_PRICE)
            val quantityColIndex = c.getColumnIndex(DBHelper.KEY_QUANTITY)
            do {
                Log.d(
                    LOG_TAG,
                    "ID = " + c.getInt(idColIndex) +
                            ", name = " + c.getString(nameColIndex) +
                            ", price = " + c.getFloat(priceColIndex) +
                            ", quantity = " + c.getInt(quantityColIndex)
                )
                list!!.add(
                    Item(
                        c.getInt(idColIndex), c.getString(nameColIndex), c.getFloat(priceColIndex),
                        c.getInt(quantityColIndex)
                    )
                )
            } while (c.moveToNext())
            data = ArrayList()
            for (i in list!!.indices) {
                m = HashMap()
                m!![ATTRIBUTE_ID] = list!![i].id
                m!![ATTRIBUTE_NAME] = list!![i].name!!
                data!!.add(m as HashMap<String, Any>)
            }

            val to = intArrayOf(R.id.text_view_id, R.id.text_view_name)
            val from = arrayOf(ATTRIBUTE_ID, ATTRIBUTE_NAME)

            simpleAdapter = SimpleAdapter(this, data, R.layout.item_back, from, to)
            listProducts!!.adapter = simpleAdapter
            textView!!.visibility = View.GONE
            listProducts!!.visibility = View.VISIBLE
            registerForContextMenu(listProducts)
        } else {
            listProducts!!.visibility = View.GONE
            textView!!.visibility = View.VISIBLE
            Log.d(LOG_TAG, "0 rows")
        }
        c.close()
        database!!.close()
    }

    fun add(v: View) {
        val i = Intent(this@BackEndActivity, BackEndChangeActivity::class.java)
        i.putExtra("change_id", 0)
        startActivity(i)
        finish()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, CM_CHANGE_ID, Menu.NONE, "Изменить запись")
        menu.add(Menu.NONE, CM_DELETE_ID, Menu.NONE, "Удалить запись")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(LOG_TAG, "clicked")
        val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo
        if (item.itemId == CM_DELETE_ID) {
            database = dbHelper!!.writableDatabase
            database!!.execSQL(
                "DELETE FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.KEY_ID
                        + " = " + list!![acmi.position].id + ";"
            )
            database!!.close()
            val toast = Toast.makeText(
                this@BackEndActivity, "Товар  с id " +
                        list!![acmi.position].id + " удален", Toast.LENGTH_SHORT
            )
            toast.setGravity(
                Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                0,
                200
            )
            toast.show()
            data!!.removeAt(acmi.position)
            list!!.removeAt(acmi.position)
            simpleAdapter!!.notifyDataSetChanged()
            return true
        }
        if (item.itemId == CM_CHANGE_ID) {
            val i = Intent(this, BackEndChangeActivity::class.java)
            i.putExtra("change_id", 1)
            i.putExtra("item_front", list!![acmi.position])
            i.putExtra("position", acmi.position)
            startActivity(i)
            finish()
            return true
        }
        return super.onContextItemSelected(item)
    }

    override fun onNoteClick(position: Int) {
        Log.d(LOG_TAG, "clicked")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_back, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_back -> {
                val i = Intent(this@BackEndActivity, StoreFrontActivity::class.java)
                startActivity(i)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        internal val CM_DELETE_ID = 1
        internal val CM_CHANGE_ID = 2
    }
}
