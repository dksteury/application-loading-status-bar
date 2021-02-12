package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R.drawable.ic_assistant_black_24dp
import com.udacity.R.string.notification_title

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(messageBody: String, context: Context, fileName: String, status: String) {

    val detailIntent = Intent(context, DetailActivity::class.java)
            .putExtra("fileName", fileName)
            .putExtra("status", status)

//    val detailIntent = Intent(context, DetailActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        putExtra("fileName", fileName)
//        putExtra("status", status)
//    }

    val detailPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id)
    )
            .setContentTitle(context
                    .getString(notification_title))
            .setSmallIcon(ic_assistant_black_24dp)
            .setContentTitle(context
                    .getString(notification_title))
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                    ic_assistant_black_24dp,
                    context.getString(R.string.notification_check_status_button),
                    detailPendingIntent
            )
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}