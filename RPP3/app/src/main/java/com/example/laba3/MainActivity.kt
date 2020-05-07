package com.example.laba3

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import androidx.appcompat.app.AppCompatActivity

import java.util.*
import java.text.SimpleDateFormat

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val arrOffFirstNames = arrayOf("Захар","Ефрем","Чарльз","Лукиллиан","Витольд","Платон","Оскар",
        "Георгий","Болеслав","Любомир","Евгений","Виталий","Жигер","Марк","Казбек")

    private val arrOffLastNames = arrayOf("Чухрай","Шевченко","Моисеев","Веселов","Герасимов","Дидовец",
        "Осипов","Дьячков","Федосеев","Соболев","Лановой","Михеев","Дзюба","Васильев","Лановой")

    private val arrOffMiddleNames = arrayOf("Сергеевич","Владимирович","Иванович","Эдуардович","Алексеевич",
        "Максимович","Алексеевич","Александрович","Михайлович","Фёдорович","Михайлович","Романович",
        "Фёдорович","Максимович","Фёдорович")

    private val dbHelper = StudentDBHelper(this)

    private val valuesList = ArrayList<ContentValues>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbWrite = dbHelper.writableDatabase
        val dbRead = dbHelper.readableDatabase

        addFiveRandomRecords()

        // Метод для запуска активити просмотра данных
        viewBDButton.setOnClickListener {
            val intent = Intent(this,CheckDBActivity::class.java)
            startActivity(intent)
        }
        // Метод который добавляет еще одного челика
        insRecButton.setOnClickListener {

            val lastName = arrOffLastNames[(arrOffLastNames.indices).shuffled().first()]
            val firstName = arrOffFirstNames[(arrOffFirstNames.indices).shuffled().first()]
            val middleName = arrOffMiddleNames[(arrOffMiddleNames.indices).shuffled().first()]
            val FIO = "${lastName} ${firstName} ${middleName}"

            val currentData = Date()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

            val value = ContentValues().apply {
                put(StudentRecord.FIO_COLUMN, FIO)
                put(StudentRecord.DATE_ADD_COLUMN, dateFormat.format(currentData))
            }

            dbWrite.insert(StudentRecord.STUDENT_TABLE, null, value)
        }

        // Метод который изменит последний элемент на Иванов Иван Иванович
        updLastRecButton.setOnClickListener {
            // Получить последний элемент
            val projection = arrayOf(BaseColumns._ID)
            val sortOrder = "${BaseColumns._ID} DESC"
            val cursor = dbRead.query(
                StudentRecord.STUDENT_TABLE,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                "1"
            )
            cursor.moveToNext()
            val lastRecordId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            cursor.close()

            // Изменить его
            val newFioData = "Иванов Иван Иванович"
            val values = ContentValues().apply {
                put(StudentRecord.FIO_COLUMN, newFioData)
            }
            val selection = "${BaseColumns._ID} = ?"
            val selectionArgs = arrayOf(lastRecordId.toString())
            dbWrite.update(
                StudentRecord.STUDENT_TABLE,
                values,
                selection,
                selectionArgs
            )
        }
    }
    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    // Метод для добавления 5 новых элементов и удаления старых
    private fun addFiveRandomRecords() {
        val dbWrite = dbHelper.writableDatabase

        dbHelper.truncate(dbWrite)
        for (i in 1..5) {
            val lastName = arrOffLastNames[(arrOffLastNames.indices).shuffled().first()]
            val firstName = arrOffFirstNames[(arrOffFirstNames.indices).shuffled().first()]
            val middleName = arrOffMiddleNames[(arrOffMiddleNames.indices).shuffled().first()]
            val FIO = "${lastName} ${firstName} ${middleName}"

            val currentData = Date()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

            valuesList.add(
                ContentValues().apply {
                    put(StudentRecord.FIO_COLUMN, FIO)
                    put(StudentRecord.DATE_ADD_COLUMN, dateFormat.format(currentData))
                }
            )
        }
        for (values in valuesList)
            dbWrite?.insert(StudentRecord.STUDENT_TABLE, null, values)

    }

}
