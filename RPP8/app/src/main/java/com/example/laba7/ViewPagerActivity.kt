package com.example.laba7

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import java.util.ArrayList

class ViewPagerActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var r: ArrayList<Item>? = null
    private var positionItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        val i = intent
        r = i.getParcelableArrayListExtra("listOfItems")
        positionItem = i.getIntExtra("position", 0)

        viewPager = findViewById(R.id.Vpager)
        val adapter = AdapterPager(this, r)
        viewPager!!.adapter = adapter
        viewPager!!.currentItem = positionItem
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this@ViewPagerActivity, StoreFrontActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@ViewPagerActivity, StoreFrontActivity::class.java)
        startActivity(i)
        finish()
    }

    companion object {

        internal val TAG = "myLogs"
    }
}
