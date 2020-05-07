package com.example.laba4

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.RemoteViews

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DayWidget : AppWidgetProvider() {
    internal val UPDATE_ALL_WIDGETS = "update_all_widgets"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        setLeftDays(context)


        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 9)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val intent = Intent(context, DayWidget::class.java)
        intent.action = UPDATE_ALL_WIDGETS

        val pIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC, c.timeInMillis,
            AlarmManager.INTERVAL_DAY, pIntent
        )
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        val intent = Intent(context, DayWidget::class.java)
        intent.action = UPDATE_ALL_WIDGETS

        val pIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action!!.equals(UPDATE_ALL_WIDGETS, ignoreCase = true)) {
            val thisAppWidget = ComponentName(
                context.packageName, javaClass.name
            )

            val appWidgetManager = AppWidgetManager
                .getInstance(context)

            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            for (appWidgetID in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        val editor = context.getSharedPreferences(
            ConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE
        ).edit()

        for (widgetID in appWidgetIds) {
            editor.remove("LONG1$widgetID")
            editor.remove("LONG2$widgetID")
        }
        editor.apply()
    }

    companion object {

        private val CHANNEL_ID = "Channel"

        private val TAG = DayWidget::class.java.simpleName
        private var startDate: Long = 0
        private var endDate: Long = 0
        private var leftDays: Long = 0

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val sharedPreferences =
                context.getSharedPreferences(ConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE)

            startDate = sharedPreferences.getLong("LONG1$appWidgetId", 0)

            if (startDate != 0L) {
                endDate = sharedPreferences.getLong("LONG2$appWidgetId", 0)
                leftDays = getDifference(startDate, endDate)
            } else {
                setLeftDays(context)
            }

            Log.e(TAG, "DAYS: $leftDays")

            var widgetText = context.getString(R.string.appwidget_text)
            widgetText += " $leftDays"

            val views = RemoteViews(context.packageName, R.layout.day_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            val editor = context.getSharedPreferences(
                ConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE
            ).edit()
            editor.remove("LONG1$appWidgetId")
            editor.apply()

            val configIntent = Intent(context, ConfigureActivity::class.java)
            configIntent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0)
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        internal fun getDifference(millis1: Long, millis2: Long): Long {
            val diff = millis2 - millis1
            return diff / (24 * 60 * 60 * 1000)
        }

        internal fun setLeftDays(context: Context) {
            val currentDate = Calendar.getInstance()
            val currentMillis = currentDate.timeInMillis
            val countDays = getDifference(currentMillis, endDate)

            if (countDays != leftDays) {
                leftDays = countDays
            }

            if (isTimeCome && countDays == 0L) {
                showNotification(context)
            }
        }

        internal val isTimeCome: Boolean
            get() {
                val strTargetTime = "09:00:00"
                val strEndBorderTime = "10:00:00"

                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

                val cal = Calendar.getInstance()
                val time = cal.time

                val strCurrentTime = dateFormat.format(time)

                try {
                    val targetTime = sdf.parse(strTargetTime)
                    val currentTime = sdf.parse(strCurrentTime)
                    val endBorderTime = sdf.parse(strEndBorderTime)

                    if (targetTime == currentTime || currentTime!!.before(endBorderTime) && currentTime.after(
                            targetTime
                        )
                    ) {
                        return true
                    }
                } catch (ex: ParseException) {
                    Log.e(TAG, " " + ex.message)
                }

                return false
            }

        internal fun showNotification(context: Context) {

            val name = "Напоминание"
            val description = "Событие наступило!"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager!!.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.watch)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(context)

            notificationManager.notify(1, builder.build())
        }
    }
}

