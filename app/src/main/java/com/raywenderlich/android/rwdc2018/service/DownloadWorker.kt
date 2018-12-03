package com.raywenderlich.android.rwdc2018.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.raywenderlich.android.rwdc2018.app.PhotosUtils

class DownloadWorker(context: Context, params: WorkerParameters)
  : Worker(context, params) {

  companion object {
    private const val TAG = "DownloadWorker"
  }

  override fun doWork(): Result {

    val needsRetry = try {
      val response = PhotosUtils.fetchJsonString()
      (response == null)
    } catch (e: InterruptedException) {
      Log.e(TAG, "Error fetchJsonString: ${e.localizedMessage}")
      true
    }

    if (needsRetry) {
      Log.i(TAG, "WorkerResult.RETRY")
      return Result.RETRY
    }

    Log.i(TAG, "WorkerResult.SUCCESS")
    return Result.SUCCESS
  }
}