package com.raywenderlich.android.rwdc2018.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class LogJobService : JobService() {

  companion object {
    private val TAG = javaClass.simpleName
  }

  override fun onCreate() {
    super.onCreate()
    Log.i(TAG, "onCreate")
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(TAG, "onDestroy")
  }

  override fun onStartJob(params: JobParameters?): Boolean {
    val runnable = Runnable {
      Thread.sleep(5000)
      jobFinished(params, false)
      Log.w(TAG, "Job finished: ${params?.jobId}")
    }

    Log.i(TAG, "Starting job: ${params?.jobId}")
    Thread(runnable).start()
    return false
  }

  override fun onStopJob(params: JobParameters?): Boolean {
    Log.d(TAG, "Stopping job: ${params?.jobId}")
    return false
  }
}
