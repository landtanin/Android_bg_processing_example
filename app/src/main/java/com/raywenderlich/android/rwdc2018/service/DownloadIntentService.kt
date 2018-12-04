package com.raywenderlich.android.rwdc2018.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.raywenderlich.android.rwdc2018.app.SongUtils

class DownloadIntentService : IntentService("DownloadIntentService") {

  companion object {
    private const val TAG = "DownloadIntentService"
    private const val ACTION_DOWNLOAD = "ACTION_DOWNLOAD"
    private const val EXTRA_URL = "EXTRA_URL"

    fun startActionDownload(context: Context, param: String) {
      val intent = Intent(context, DownloadIntentService::class.java).apply {
        action = ACTION_DOWNLOAD
        putExtra(EXTRA_URL, param)
      }
      context.startService(intent)
    }

  }

  override fun onCreate() {
    super.onCreate()
    Log.i(TAG, "Creating service")
  }

  override fun onDestroy() {
    Log.i(TAG, "Destroying service")
    super.onDestroy()
  }

  override fun onHandleIntent(intent: Intent?) {
    when (intent?.action) {
      ACTION_DOWNLOAD -> {
        handleActionDownload(intent.getStringExtra(EXTRA_URL))
      }
    }
  }

  private fun handleActionDownload(param: String) {
    Log.i(TAG, "start download $param")
    SongUtils.download(param)
    Log.i(TAG, "end download $param")
  }

}
