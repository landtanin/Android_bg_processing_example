package com.raywenderlich.android.rwdc2018.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.raywenderlich.android.rwdc2018.app.PhotosUtils

class PhotosJobService : JobService() {

  companion object {

    private const val TAG = "PhotosJobService"

  }

  override fun onStartJob(params: JobParameters?): Boolean {
    val runnable = Runnable {
      val reScheduled: Boolean
      reScheduled = try {
        val fetchJsonString = PhotosUtils.fetchJsonString()
        (fetchJsonString == null)
      } catch (e: InterruptedException) {
        Log.e(TAG, "failed with error: ${e.localizedMessage}")
        true
      }

      Log.i(TAG, "Job finished: ${params?.jobId}, needsReschedule = ${reScheduled}")
      jobFinished(params, reScheduled)
    }

    val thread = Thread(runnable)
    thread.start()

    // return true to reschedule it (once every 15 minutes)
    return true
  }

  override fun onStopJob(params: JobParameters?): Boolean {
    Log.d(TAG, "onStopJob: ${params?.jobId}")
    return false
  }
}
