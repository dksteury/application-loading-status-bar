package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedGitHubRepository: String? = null
    private var selectedFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
        )

        custom_button.setOnClickListener {
            download()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_glide ->
                    if (checked) {
                        selectedGitHubRepository = URL_GLIDE
                        selectedFileName = getString(R.string.radio_glide_text)
                    }
                R.id.radio_udacity ->
                    if (checked) {
                        selectedGitHubRepository = URL_UDACITY
                        selectedFileName = getString(R.string.radio_udacity_text)
                    }
                R.id.radio_retrofit ->
                    if (checked) {
                        selectedGitHubRepository = URL_RETROFIT
                        selectedFileName = getString(R.string.radio_retrofit_text)
                    }
                else -> {
                    selectedGitHubRepository = null
                    selectedFileName = null
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.cancelNotifications()
            val downloadId = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query()
                    .setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(
                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                )
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        custom_button.buttonState = ButtonState.Completed
                        notificationManager.sendNotification(getString(R.string.notification_description), context, selectedFileName.toString(), getString(R.string.success_text))
                    }
                    DownloadManager.STATUS_FAILED -> {
                        custom_button.buttonState = ButtonState.Completed
                        notificationManager.sendNotification(getString(R.string.notification_description), context, selectedFileName.toString(), getString(R.string.fail_text))
                    }
                }
            }
        }
    }

    private fun download() {
        if (selectedGitHubRepository != null) {
//            val direct = File(getExternalFilesDir(null), "/repos")
//            if (!direct.exists()) {
//                direct.mkdirs()
//            }
            val request =
                    DownloadManager.Request(Uri.parse(selectedGitHubRepository))
                            .setTitle(getString(R.string.app_name))
                            .setDescription(getString(R.string.app_description))
                            .setRequiresCharging(false)
//                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/repos/reopsitory.zip" )
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                    downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            custom_button.buttonState = ButtonState.Loading
        } else {
            Toast.makeText(this, getString(R.string.noRepotSelectedText), Toast.LENGTH_LONG).show()
            custom_button.buttonState = ButtonState.Clicked
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
                    .apply {
                        setShowBadge(false)
                    }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableLights(true)
            notificationChannel.description = "Description"

            notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            Log.i("createChannel", "Fail")
        }
    }

    companion object {
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
