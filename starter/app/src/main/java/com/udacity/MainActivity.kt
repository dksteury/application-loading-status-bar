package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedGitHubRepository: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = getString(R.string.toolbar_title_main)
        setSupportActionBar(toolbar)


        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            Log.i("receiver", "Start! $selectedGitHubRepository)")
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
                        selectedGitHubRepository = "https://github.com/bumptech/glide/archive/master.zip"
                    }
                R.id.radio_udacity ->
                    if (checked) {
                        selectedGitHubRepository = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                    }
                R.id.radio_retrofit ->
                    if (checked) {
                        selectedGitHubRepository = "https://github.com/square/retrofit/archive/master.zip"
                    }
                else -> selectedGitHubRepository = null
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
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
                        Toast.makeText(applicationContext, "Success!", Toast.LENGTH_LONG).show()
                        Log.i("receiver", "Success!")
                        custom_button.buttonState = ButtonState.Completed
                    }
                    DownloadManager.STATUS_FAILED -> {
                        Toast.makeText(applicationContext, "Fail!", Toast.LENGTH_LONG).show()
                        Log.i("receiver", "Fail!")
                        custom_button.buttonState = ButtonState.Completed
                    }
                }
            }
        }
    }

    private fun download() {
        if (selectedGitHubRepository != null) {
//            notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
//            createChannel(getString(R.string.githubRepo_notification_channel_id), getString(R.string.githubRepo_notification_channel_name))
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
            Toast.makeText(applicationContext, getString(R.string.noRepotSelectedText), Toast.LENGTH_LONG).show()
            Log.i("download", "should see a Toast!")
            custom_button.buttonState = ButtonState.Clicked
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
