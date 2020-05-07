package com.example.laba5

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myPreferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        catsBreedEditText.setText(myPreferences.getString("catBreed", ""))

        searchButton.setOnClickListener {
            val myEditor = myPreferences.edit()
            myEditor.remove("catBreed")
            myEditor.putString("catBreed", catsBreedEditText.text.toString())
            myEditor.apply()

            val intent = Intent(this, CheckCatsActivity::class.java)
            val catBreed = catsBreedEditText.text.toString()
            intent.putExtra("catBreed", catBreed)

            startActivity(intent)
        }

        openLastTenButton.setOnClickListener {
            val intent = Intent(this, CheckLastTenLikesActivity::class.java)
            startActivity(intent)
        }
    }
}
