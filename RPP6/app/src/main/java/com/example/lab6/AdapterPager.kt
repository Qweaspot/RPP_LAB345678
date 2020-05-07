package com.example.lab6

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter

import java.util.ArrayList

class AdapterPager(private val context: Context, private val list: ArrayList<Item>) :
    PagerAdapter() {
    private val dbHelper: DBHelper
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

        name.text = list[position].name
        price.text = java.lang.Float.toString(list[position].price)
        quantity.text = Integer.toString(list[position].quantity)
        btn_buy.setOnClickListener {
            database = dbHelper.writableDatabase
            database!!.execSQL(
                "UPDATE " + DBHelper.TABLE_PRODUCTS + " SET " +
                        DBHelper.KEY_QUANTITY + " = " + DBHelper.KEY_QUANTITY + " - 1"
                        + " WHERE " + DBHelper.KEY_ID + " = " + list[position].id + ";"
            )
            database!!.close()
            val toast = Toast.makeText(
                context, "Товар \"" + list[position].name
                        + "\" куплен", Toast.LENGTH_SHORT
            )
            toast.setGravity(
                Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                0,
                200
            )
            toast.show()
            list[position].quantity = list[position].quantity - 1
            quantity.text = Integer.toString(list[position].quantity)
            if (list[position].quantity <= 0) {
                btn_buy.isEnabled = false
                database = dbHelper.writableDatabase
                database!!.execSQL(
                    "DELETE FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.KEY_ID
                            + " = " + list[position].id + ";"
                )
                database!!.close()
                list.removeAt(position)
            }
            notifyDataSetChanged()
        }
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }


}
