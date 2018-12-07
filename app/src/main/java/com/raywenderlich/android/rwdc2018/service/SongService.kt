package com.raywenderlich.android.rwdc2018.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import com.raywenderlich.android.rwdc2018.app.RWDC2018Application
import com.raywenderlich.android.rwdc2018.app.SongUtils

class SongService : Service() {

  private lateinit var player: MediaPlayer

  override fun onBind(intent: Intent): IBinder? {
    return null // service is not bound to any activity
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

}
