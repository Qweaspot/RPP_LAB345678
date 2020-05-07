package com.example.laba4

import android.app.Activity
import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import java.util.Calendar


class ConfigureActivity : Activity() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var startDate: Long = 0
    private var endDate: Long = 0

    private var resultValue: Intent? = null
    private var sharedPreferences: SharedPreferences? = null
    private val calendar = Calendar.getInstance()

    internal var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setInitialDates()

            sharedPreferences!!.edit()
                .putLong("LONG1$widgetID", startDate)
                .apply()
            sharedPreferences!!.edit()
                .putLong("LONG2$widgetID", endDate)
                .apply()

            DayWidget.updateAppWidget(
                this@ConfigureActivity,
                AppWidgetManager.getInstance(this@ConfigureActivity), widgetID
            )
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.day_widget_configure)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        resultValue = Intent()
        resultValue!!.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)

        setResult(Activity.RESULT_CANCELED, resultValue)
        sharedPreferences = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)

        setDate()
    }

    fun setDate() {
        val dialog = DatePickerDialog(
            this@ConfigureActivity, d,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "") { dialog, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                finish()
            }
        }
    }

    private fun setInitialDates() {
        val currentMillis = Calendar.getInstance()
        val millis1 = currentMillis.timeInMillis
        val millis2 = calendar.timeInMillis

        startDate = millis1
        endDate = millis2
    }

    companion object {

        val WIDGET_PREF = "WIDGET_PREF"
    }
}

