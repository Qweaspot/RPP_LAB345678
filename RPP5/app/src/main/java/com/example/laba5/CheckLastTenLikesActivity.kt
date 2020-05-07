package com.example.laba5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_check_cats.*

class CheckLastTenLikesActivity : AppCompatActivity() {

    private val dbHelper = CatsDBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_cats)
        loadCats()
    }

    private fun loadCats(){
        val dbRead = dbHelper.readableDatabase
        val catsDBList = ArrayList<CatDataClass>()

        val projection = arrayOf(CatRecord.IMAGE_URL_COLUMN)
        val cursor = dbRead.query(
            CatRecord.CAT_TABLE,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor){
            while (moveToNext()){
                val imageUrl = getString(getColumnIndex(CatRecord.IMAGE_URL_COLUMN))
                catsDBList.add(CatDataClass("", imageUrl))
            }
        }
        cursor.close()

        catsListView.adapter = LikesCatsAdapter(this, catsDBList)
    }
}