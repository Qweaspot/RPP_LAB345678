package com.example.laba7

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Message
import android.text.InputFilter
import android.text.Spanned
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.util.ArrayList
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class BackEndChangeActivity : AppCompatActivity() {
    internal var editTextName: EditText
    internal var editTextPrice: EditText
    internal var editTextQuantity: EditText
    internal var buttonChange: Button
    internal var buttonCancel: Button
    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null
    internal var item: Item? = null

    private var list: ArrayList<Item>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_change)
        editTextName = findViewById(R.id.edit_text_name)
        editTextPrice = findViewById(R.id.edit_text_price)
        editTextPrice.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(5, 2))
        editTextQuantity = findViewById(R.id.edit_text_quantity)
        buttonChange = findViewById(R.id.btn_change)
        buttonCancel = findViewById(R.id.btn_cancel)

        dbHelper = DBHelper(this@BackEndChangeActivity)
        database = dbHelper!!.writableDatabase
        list = ArrayList()

        val i = intent
        if (i.getIntExtra("change_id", 0) == 0) {
            buttonChange.setText(R.string.action_back_add)
            buttonChange.setOnClickListener {
                if (editTextName.length() != 0 && editTextPrice.length() != 0 &&
                    editTextQuantity.length() != 0
                ) {
                    database = dbHelper!!.writableDatabase
                    database!!.execSQL(
                        "INSERT INTO " + DBHelper.TABLE_PRODUCTS +
                                " (" + DBHelper.KEY_NAME + ", " + DBHelper.KEY_PRICE + ", " +
                                DBHelper.KEY_QUANTITY + ") VALUES( \'" + editTextName.text.toString()
                                + "\', " + java.lang.Float.valueOf(editTextPrice.text.toString()) + ", " +
                                Integer.valueOf(editTextQuantity.text.toString())
                                + ");"
                    )
                    database!!.close()
                    val toast = Toast.makeText(
                        this@BackEndChangeActivity,
                        "Товар добавлен", Toast.LENGTH_SHORT
                    )
                    toast.setGravity(
                        Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                        0,
                        200
                    )
                    toast.show()
                    val i = Intent(this@BackEndChangeActivity, BackEndActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val toast = Toast.makeText(
                        this@BackEndChangeActivity,
                        "Заполните все поля ввода", Toast.LENGTH_SHORT
                    )
                    toast.setGravity(
                        Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                        0,
                        200
                    )
                    toast.show()
                }
            }
        } else if (i.getIntExtra("change_id", 0) == 1) {
            buttonChange.setText(R.string.action_back_save)
            item = i.getParcelableExtra("item_front")
            editTextName.setText(item!!.name)
            editTextPrice.setText(java.lang.Float.toString(item!!.price))
            editTextQuantity.setText(Integer.toString(item!!.quantity))
            buttonChange.setOnClickListener {
                if (editTextName.length() != 0 && editTextPrice.length() != 0 &&
                    editTextQuantity.length() != 0
                ) {
                    val runnable = Runnable {
                        list = i.getParcelableArrayListExtra("listOfProducts")
                        val position = i.getIntExtra("position", 0)
                        val j = Intent(this@BackEndChangeActivity, BackEndActivity::class.java)
                        startActivity(j)
                        finish()
                        try {
                            TimeUnit.SECONDS.sleep(3)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                        list!![position].name = editTextName.text.toString()
                        list!![position].price =
                            java.lang.Float.parseFloat(editTextPrice.text.toString())
                        list!![position].quantity =
                            Integer.parseInt(editTextQuantity.text.toString())

                        val msg1 = StoreFrontActivity.handler1.obtainMessage()
                        val msg = BackEndActivity.handler.obtainMessage()
                        var msg2 = Message()
                        if (AdapterPager.handler != null) {
                            msg2 = AdapterPager.handler.obtainMessage()
                        }
                        val bundle2 = Bundle()
                        val bundle1 = Bundle()
                        val bundle = Bundle()
                        database = dbHelper!!.writableDatabase
                        database!!.execSQL(
                            "UPDATE " + DBHelper.TABLE_PRODUCTS + " SET " +
                                    DBHelper.KEY_NAME + "= \"" + editTextName.text.toString() + "\", " +
                                    DBHelper.KEY_PRICE + "=" + java.lang.Float.parseFloat(
                                editTextPrice.text.toString()
                            ) + ", " +
                                    DBHelper.KEY_QUANTITY + "=" + Integer.parseInt(editTextQuantity.text.toString()) + " WHERE "
                                    + DBHelper.KEY_ID + "=" + item!!.id + ";"
                        )

                        database!!.close()
                        var index = 0
                        database = dbHelper!!.readableDatabase
                        val cursor = database!!.query(
                            DBHelper.TABLE_PRODUCTS,
                            arrayOf(DBHelper.KEY_ID),
                            DBHelper.KEY_NAME + " = ? AND " +
                                    DBHelper.KEY_PRICE + " = ? AND " +
                                    DBHelper.KEY_QUANTITY + " = ? ",
                            arrayOf(
                                list!![position].name,
                                list!![position].price.toString(),
                                list!![position].quantity.toString()
                            ), null, null, null, null
                        )
                        if (cursor.moveToFirst()) {
                            index = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID))
                            list!![position].id = index
                        }
                        cursor.close()
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
                        bundle1.putParcelableArrayList("listOfProducts", arr)
                        bundle.putParcelableArrayList("listOfProducts", list)
                        bundle1.putParcelable("item", list!![position])
                        bundle.putInt("position", position)
                        msg1.data = bundle1
                        msg.data = bundle
                        if (AdapterPager.handler != null) {
                            bundle2.putParcelable("item", list!![position])
                            bundle2.putInt("position", position)
                            msg2.data = bundle2
                            AdapterPager.handler.sendMessage(msg2)
                        }
                        StoreFrontActivity.handler1.sendMessage(msg1)
                        BackEndActivity.handler.sendMessage(msg)
                    }
                    val thread = Thread(runnable)
                    thread.start()

                } else {
                    val toast = Toast.makeText(
                        this@BackEndChangeActivity,
                        "Заполните все поля ввода", Toast.LENGTH_SHORT
                    )
                    toast.setGravity(
                        Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                        0,
                        200
                    )
                    toast.show()
                }
            }


        }
        buttonCancel.setOnClickListener {
            val i = Intent(this@BackEndChangeActivity, BackEndActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@BackEndChangeActivity, BackEndActivity::class.java)
        startActivity(i)
        finish()
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
                val i = Intent(this@BackEndChangeActivity, StoreFrontActivity::class.java)
                startActivity(i)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}

internal class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {
    private val mPattern: Pattern

    init {
        mPattern =
            Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val matcher = mPattern.matcher(dest)
        return if (!matcher.matches()) "" else null
    }
}