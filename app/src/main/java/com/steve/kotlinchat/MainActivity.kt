package com.steve.kotlinchat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    lateinit var btnSendMessage: Button
    lateinit var textMsg: EditText

    val CHANNEL_ID="channelID"
    val CHANNEL_NAME="channelName"
    val NOTIFICATION_ID=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        createNotificationChannel()

        val intent= Intent(this,MainActivity::class.java)
        val pendingIntent= TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        btnSendMessage=findViewById(R.id.buttonSend)
        textMsg=findViewById(R.id.chatEdit)

        val notification= NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Kotlin Chat")
                .setContentText("${textMsg}")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

        val notificationManager= NotificationManagerCompat.from(this)

        btnSendMessage.setOnClickListener {
            notificationManager.notify(NOTIFICATION_ID,notification)
            val text=textMsg.text.toString()
        }
    }

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel= NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor= Color.GREEN
                enableLights(true)
            }

            val manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

    }
}