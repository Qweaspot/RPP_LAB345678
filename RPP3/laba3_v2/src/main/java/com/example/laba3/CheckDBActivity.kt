package com.example.laba3

import android.os.Bundle
import android.provider.BaseColumns
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.check_db_activity.*

class CheckDBActivity : AppCompatActivity() {

    private val dbHelper = StudentDBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_db_activity)
        loadDBList()

    }
    // Метод для загрузки данных из бд и вставка в лист
    private fun loadDBList(){
        val dbRead = dbHelper.readableDatabase
        val dbDataList = ArrayList<DBItem>()
        val projection = arrayOf(
            BaseColumns._ID,
            StudentRecord.LAST_NAME_COLUMN,
            StudentRecord.FIRST_NAME_COLUMN,
            StudentRecord.MIDDLE_NAME_COLUMN,
            StudentRecord.DATE_ADD_COLUMN
        )
        val cursor = dbRead.query(
            StudentRecord.STUDENT_TABLE,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor){
            while (moveToNext()){
                val itemId = getInt(getColumnIndex(BaseColumns._ID))
                val lastName = getString(getColumnIndex(StudentRecord.LAST_NAME_COLUMN))
                val firstName = getString(getColumnIndex(StudentRecord.FIRST_NAME_COLUMN))
                val middleName = getString(getColumnIndex(StudentRecord.MIDDLE_NAME_COLUMN))
                val dateOfAdd = getString(getColumnIndex(StudentRecord.DATE_ADD_COLUMN))
                dbDataList.add(DBItem(itemId, lastName, firstName, middleName, dateOfAdd))
            }
        }
        cursor.close()

        dbListView.adapter = DBListAdapter(this, dbDataList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}