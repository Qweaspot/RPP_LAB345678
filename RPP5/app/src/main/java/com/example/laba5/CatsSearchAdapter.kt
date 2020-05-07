package com.example.laba5

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cats_list_item.view.*

class CatsSearchAdapter(val context: Context, val catsList: ArrayList<CatDataClass>): BaseAdapter() {
    private val dbHelper = CatsDBHelper(context)
    private val dbWrite = dbHelper.writableDatabase

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.cats_list_item, parent, false)

        val catImageView  = view.findViewById(R.id.catImage) as ImageView

        if (isExist(catsList[position].catId)){
            view.likeButton.setBackgroundColor(Color.GREEN)
            view.likeButton.isEnabled = true
            view.dislikeButton.isEnabled = false
        }

        Picasso.get().load(catsList[position].catPictUrl).into(catImageView)

        view.likeButton.setOnClickListener {
            removeElevenRecord()
            
            view.likeButton.setBackgroundColor(Color.GREEN)
            view.likeButton.isEnabled = false
            view.dislikeButton.isEnabled = false

            val catApiId = catsList[position].catId
            val catImageUrl = catsList[position].catPictUrl
            val value = ContentValues().apply {
                put(CatRecord.CAT_ID_COLUMN, catApiId)
                put(CatRecord.IMAGE_URL_COLUMN, catImageUrl)
            }
            dbWrite.insert(CatRecord.CAT_TABLE, null, value)
        }
        view.dislikeButton.setOnClickListener {
            view.dislikeButton.setBackgroundColor(Color.RED)
            view.likeButton.isEnabled = false
            view.dislikeButton.isEnabled = false
        }

        return view
    }

    private fun isExist(catApiId : String) : Boolean {
        val dbRead = dbHelper.readableDatabase

        val projection = arrayOf(CatRecord.ID_COLUMN)
        val selection = "${CatRecord.CAT_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(catApiId)
        val cursor = dbRead.query(
            CatRecord.CAT_TABLE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        cursor.moveToNext()
        val countOfRecords = cursor.count
        cursor.close()
        return countOfRecords != 0
    }
    private fun removeElevenRecord(){
        val dbRead = dbHelper.readableDatabase

        var projection = arrayOf(CatRecord.IMAGE_URL_COLUMN)
        var cursor = dbRead.query(
            CatRecord.CAT_TABLE,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val countOfRecords = cursor.count
        cursor.close()

        if (countOfRecords >= 10) {
            projection = arrayOf(CatRecord.ID_COLUMN)
            cursor = dbRead.query(
                CatRecord.CAT_TABLE,
                projection,
                null,
                null,
                null,
                null,
                null,
                "1"
            )
            cursor.moveToNext()
            val firstRecordId = cursor.getString(cursor.getColumnIndex(CatRecord.ID_COLUMN))
            cursor.close()
            val selection = "${CatRecord.ID_COLUMN} = ?"
            val selectionArgs = arrayOf(firstRecordId)
            dbWrite.delete(
                CatRecord.CAT_TABLE,
                selection,
                selectionArgs
            )
        }
    }

    override fun getItem(position: Int): Any {
        return catsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return catsList.size
    }

}