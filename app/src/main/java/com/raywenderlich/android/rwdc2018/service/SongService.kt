package com.raywenderlich.android.rwdc2018.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.raywenderlich.android.rwdc2018.R
import com.raywenderlich.android.rwdc2018.app.RWDC2018Application
import com.raywenderlich.android.rwdc2018.app.SongUtils
import com.raywenderlich.android.rwdc2018.ui.main.MainActivity

class SongService : Service() {

  private lateinit var player: MediaPlayer

  companion object {

    const val CHANNEL_ID = "SONG_CHANNEL_ID"
  }

  override fun onBind(intent: Intent): IBinder? {
    return null // service is not bound to any activity
  }

  override fun onCreate() {
    super.onCreate()

    startForeground(1000, createNotification())
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

    player = MediaPlayer.create(this, Uri.fromFile(SongUtils.songFile()))
    player.isLooping = true
    player.start()
    RWDC2018Application.isSongPlaying = true

    return START_STICKY // the service should run at any arbitrary time
  }

  override fun onDestroy() {
    player.stop()
    RWDC2018Application.isSongPlaying = false
    super.onDestroy()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun createChannel() {

    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(CHANNEL_ID, this.getString(R.string.media_playback),
      NotificationManager.IMPORTANCE_LOW)
    channel.setShowBadge(false)
    channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    notificationManager.createNotificationChannel(channel)

  }

  fun createNotification(): Notification {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel()
    }

    val notiIntent = Intent(this, MainActivity::class.java)
    notiIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    val pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, 0)

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setColor(ContextCompat.getColor(this, android.R.color.background_dark))
      .setContentIntent(pendingIntent)
      .setSmallIcon(R.drawable.rwdevcon_logo_24px)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setOnlyAlertOnce(true)
      .setContentTitle(getString(R.string.challenge))
      .build()

  }

}
