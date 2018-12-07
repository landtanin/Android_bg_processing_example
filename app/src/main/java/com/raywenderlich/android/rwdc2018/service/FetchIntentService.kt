package com.raywenderlich.android.rwdc2018.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.raywenderlich.android.rwdc2018.app.PhotosUtils

class FetchIntentService : IntentService("FetchIntentService") {

  companion object {
    private const val TAG = "FetchIntentService"
    private const val ACTION_DOWNLOAD_PHOTO = "ACTION_DOWNLOAD_PHOTO"
    private const val EXTRA_URL = "EXTRA_URL"
    const val PHOTO_DOWNLOAD_COMPLETE = "PHOTO_DOWNLOAD_COMPLETE"
    const val PHOTO_DOWNLOAD_COMPLETE_KEY = "PHOTO_DOWNLOAD_COMPLETE_KEY"

    fun startActionPhotosDownload(context: Context, param: String) {
      val intent = Intent(context, FetchIntentService::class.java).apply {
        action = ACTION_DOWNLOAD_PHOTO
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
      ACTION_DOWNLOAD_PHOTO -> {
        handleActionDownloadPhotos(intent.getStringExtra(FetchIntentService.EXTRA_URL))
      }
    }
  }

  private fun handleActionDownloadPhotos(param: String) {
    Log.i(TAG, "start download $param")
    PhotosUtils.fetchJsonString()
    Log.i(TAG, "end download $param")

    Log.i(TAG, "Sending broadcast for $param")
    broadcastPhotoDownloadComplete(param)
  }

  private fun broadcastPhotoDownloadComplete(param: String) {
    val intent = Intent(PHOTO_DOWNLOAD_COMPLETE)
    intent.putExtra(PHOTO_DOWNLOAD_COMPLETE_KEY, param)
    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
  }

}
