package com.example.laba5

import android.os.AsyncTask
import android.os.Bundle
import android.widget.AbsListView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_check_cats.*
import org.json.JSONArray
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class CheckCatsActivity : AppCompatActivity() {
    private val catsList = ArrayList<CatDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_cats)

        val catBreed = intent.extras!!.getString("catBreed")
        var apiUrl = "https://api.thecatapi.com/v1/images/search?breed_ids=${catBreed}&limit=7&page=0"

        AsyncTaskGetJsonData().execute(apiUrl)
        val catAdapter = CatsSearchAdapter(this, catsList)
        catsListView.adapter = catAdapter

        var countOfPages = 0

        catsListView.setOnScrollListener(object : AbsListView.OnScrollListener{
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
               if (firstVisibleItem == totalItemCount-3){
                   apiUrl = "https://api.thecatapi.com/v1/images/search?breed_ids=${catBreed}&limit=7&page=${countOfPages++}"
                   AsyncTaskGetJsonData().execute(apiUrl)
               }
            }
        })

    }

    inner class AsyncTaskGetJsonData : AsyncTask<String, String, String>(){
        override fun doInBackground(vararg params: String?): String {
            val jsonData : String
            val connection = URL(params[0]).openConnection() as HttpsURLConnection
            try {
                connection.connect()
                jsonData = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            }finally {
                connection.disconnect()
            }
            return jsonData
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            loadCats(result)
        }
    }

    private fun loadCats(jsonData : String?){
        val jsonArray = JSONArray(jsonData)
        val isCatListEmpty = catsList.isEmpty()

        var jsonLineIndex = 1
        while (jsonLineIndex < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(jsonLineIndex)
            catsList.add(
                CatDataClass(
                    jsonObject.getString("id"),
                    jsonObject.getString("url")
                )
            )
            jsonLineIndex++
        }
        val catAdapter = CatsSearchAdapter(this, catsList)
        if (isCatListEmpty)
            catsListView.adapter = catAdapter
        else
            catAdapter.notifyDataSetChanged()
    }
}