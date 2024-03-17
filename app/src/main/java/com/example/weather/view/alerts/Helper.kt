package com.example.weather.view.alerts
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.text.format.DateFormat.getTimeFormat
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import androidx.core.content.ContextCompat.getString
import com.example.weather.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

private const val CHANNEL_ID = "my_channel_id"
private const val NOTIFICATION_ID = 1
fun sendNotification(context: Context, message:String) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

//    val intent = ReminderDescriptionActivity.newIntent(context.applicationContext, reminderDataItem)

//    //create a pending intent that opens ReminderDescriptionActivity when the user clicks on the notification
//    val stackBuilder = TaskStackBuilder.create(context)
//        .addParentStack(ReminderDescriptionActivity::class.java)
//        .addNextIntent(intent)
//    val notificationPendingIntent = stackBuilder
//        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

//    build the notification object with the data to be shown
    val notification = Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(context,R.string.app_name))
        .setContentText(message)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_ALL)
        .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
//        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(0, notification)
}

fun TextView.setTime(timeInSecond: Int?, timeZone: TimeZone) {
    text = getTimeFormat(timeInSecond?.times(1000L) ?: -1,timeZone)
}
fun getTimeFormat(timeInMilliSecond: Long,timeZone : TimeZone): String {
    val calendar = GregorianCalendar(timeZone)
    calendar.timeInMillis = timeInMilliSecond
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    convertFormat.timeZone = timeZone
    return convertFormat.format(calendar.time).toString()
}
fun getADateFormat(timeInMilliSecond: Long):String{
    val pattern = "dd MMMM"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale(getLanguageLocale()))
    return simpleDateFormat.format(Date(timeInMilliSecond))
}
fun getLanguageLocale(): String {
    return AppCompatDelegate.getApplicationLocales().toLanguageTags()
}
fun TextView.setTime(timeInMilliSecond: Long) {
    text = getTimeFormat(timeInMilliSecond)
}
fun getTimeFormat(timeInMilliSecond: Long): String {
    val date = Date(timeInMilliSecond)
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    return convertFormat.format(date).toString()
}
fun TextView.setDate(timeInMilliSecond: Long){
    text = getADateFormat(timeInMilliSecond)
}