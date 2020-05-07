package com.example.laba7

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

import java.util.ArrayList
import java.util.concurrent.TimeUnit

class AdapterPager(private val context: Context, private var list: ArrayList<Item>?) :
    PagerAdapter() {
    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null

    init {
        this.dbHelper = DBHelper(context)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.page, container, false)
        val name = view.findViewById<TextView>(R.id.vp_name)
        val price = view.findViewById<TextView>(R.id.vp_price)
        val quantity = view.findViewById<TextView>(R.id.vp_quantity)
        val btn_buy = view.findViewById<Button>(R.id.vp_btn_buy)

        name.text = list!![position].name
        price.text = java.lang.Float.toString(list!![position].price)
        quantity.text = Integer.toString(list!![position].quantity)
        if (list!![position].quantity == 0) {
            btn_buy.isEnabled = false
        } else
            btn_buy.isEnabled = true
        btn_buy.setOnClickListener {
            val runnable = Runnable {
                try {
                    TimeUnit.SECONDS.sleep(3)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (list!!.size > 0) {
                    val msg = StoreFrontActivity.handler.obtainMessage()
                    val msg1 = handler1.obtainMessage()
                    var msg2 = Message()
                    if (BackEndActivity.handler != null) {
                        msg2 = BackEndActivity.handler.obtainMessage()
                    }
                    val bundle = Bundle()
                    val bundle1 = Bundle()
                    dbHelper = DBHelper(context)
                    database = dbHelper!!.writableDatabase
                    if (list!![position].quantity <= 0) {
                        database!!.execSQL(
                            "UPDATE " + DBHelper.TABLE_PRODUCTS + " SET " +
                                    DBHelper.KEY_QUANTITY + " = 0"
                                    + " WHERE " + DBHelper.KEY_ID + " = " + list!![position].id + ";"
                        )
                        list!!.removeAt(position)
                    } else {
                        database!!.execSQL(
                            "UPDATE " + DBHelper.TABLE_PRODUCTS + " SET " +
                                    DBHelper.KEY_QUANTITY + " = " + DBHelper.KEY_QUANTITY + " - 1"
                                    + " WHERE " + DBHelper.KEY_ID + " = " + list!![position].id + ";"
                        )
                        list!![position].quantity = list!![position].quantity - 1
                        bundle.putInt("position", position)
                        bundle1.putInt("position", position)
                    }
                    database!!.close()
                    val arr = ArrayList<Item>()
                    database = dbHelper!!.writableDatabase
                    val c = database!!.rawQuery(StoreFrontActivity.query, null)
                    if (c.moveToFirst()) {
                        val idColIndex = c.getColumnIndex(DBHelper.KEY_ID)
                        val nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME)
                        val priceColIndex = c.getColumnIndex(DBHelper.KEY_PRICE)
                        val quantityColIndex = c.getColumnIndex(DBHelper.KEY_QUANTITY)
                        do {
                            arr.add(
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
                    bundle.putParcelableArrayList("listOfProducts", arr)
                    bundle.putParcelable("item", list!![position])
                    bundle1.putParcelableArrayList("listOfProducts", list)
                    msg.data = bundle
                    msg1.data = bundle1
                    if (BackEndActivity.handler != null) {
                        msg2.data = bundle1
                        BackEndActivity.handler.sendMessage(msg2)
                    }
                    StoreFrontActivity.handler.sendMessage(msg)
                    handler1.sendMessage(msg1)
                }
            }
            val thread = Thread(runnable)
            thread.start()
            notifyDataSetChanged()
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                var T = true
                var pos = 0
                println("работает")
                val item = bundle.getParcelable<Item>("item")
                for (i in list!!) {
                    if (i.id == item!!.id) {
                        pos = list!!.indexOf(i)
                        list!![pos] = item
                        T = false
                        break
                    }
                }
                if (T) {
                    list!!.add(item)
                    pos = list!!.size - 1
                }
                if (list!![pos].quantity <= 0) {
                    val btn_buy = view.findViewById<Button>(R.id.vp_btn_buy)
                    btn_buy.isEnabled = false
                }
                val n = view.findViewById<TextView>(R.id.vp_name)
                n.text = list!![pos].name
                val p = view.findViewById<TextView>(R.id.vp_price)
                p.text = list!![pos].price.toString()
                val q = view.findViewById<TextView>(R.id.vp_quantity)
                q.text = list!![pos].quantity.toString()
                notifyDataSetChanged()
            }

        }

        handler1 = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                list = bundle.getParcelableArrayList("listOfProducts")
                if (list!!.size > 0) {
                    val p = bundle.getInt("position")
                    println("уменьшить $p")
                    if (list!![p].quantity <= 0) {
                        val btn_buy = view.findViewById<Button>(R.id.vp_btn_buy)
                        btn_buy.isEnabled = false
                    }
                    val q = view.findViewById<TextView>(R.id.vp_quantity)
                    q.text = list!![p].quantity.toString()
                    notifyDataSetChanged()
                }

            }
        }

        container.addView(view)
        return view

    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return list!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    companion object {
        var handler1: Handler
        var handler: Handler
    }


}
