package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.R.color.colorPrimaryDark
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra("fileName")
        val status = intent.getStringExtra("status")

        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        findViewById<TextView>(R.id.file_name_text).apply {
            text = fileName
        }

        findViewById<TextView>(R.id.status_text).apply {
            text = status
            if (status == getString(R.string.fail_text)) {
                setTextColor(Color.RED)
            } else {
                setTextColor(getColor(colorPrimaryDark))
            }
        }
    }

    fun onFabButtonClicked(view: View) {
        val intent = Intent(this, MainActivity::class.java)
//        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
