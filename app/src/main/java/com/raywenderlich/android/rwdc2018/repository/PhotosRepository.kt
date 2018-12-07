/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.raywenderlich.android.rwdc2018.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.raywenderlich.android.rwdc2018.app.PhotosUtils
import com.raywenderlich.android.rwdc2018.app.RWDC2018Application
import com.raywenderlich.android.rwdc2018.service.DownloadWorker
import com.raywenderlich.android.rwdc2018.service.FetchIntentService
import java.util.concurrent.TimeUnit

class PhotosRepository : Repository {
  private val photosLiveData = MutableLiveData<List<String>>()

  private val bannerLiveData = MutableLiveData<String>()
  companion object {

    private const val PHOTO_DOWNLOAD_TAG = "PHOTO_DOWNLOAD_TAG"
  }
  init {
//    scheduleFetchJob()
//    scheduleLogJob()

    schedulePeriodicWork()

  }

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      val param = intent?.extras?.getString(FetchIntentService.PHOTO_DOWNLOAD_COMPLETE_KEY)
      Log.i("PhotosRepository", "Receiver received for param $param")

      FetchPhotoAsyncTask { photos ->
        photosLiveData.value = photos
      }.execute()

    }
  }

  override fun registerBroadcastReceiver() {
    LocalBroadcastManager.getInstance(RWDC2018Application.getAppContext())
      .registerReceiver(receiver, IntentFilter(FetchIntentService.PHOTO_DOWNLOAD_COMPLETE))
  }

  override fun unRegisterBroadcastReceiver() {
    LocalBroadcastManager.getInstance(RWDC2018Application.getAppContext())
      .unregisterReceiver(receiver)
  }

  override fun getPhotos(): LiveData<List<String>> {
//    fetchPhotoData()

//    FetchPhotoAsyncTask { photos ->
//      photosLiveData.value = photos
//    }.execute()

    return photosLiveData
  }

//  private fun fetchPhotoData() {
//
//    val runnable = Runnable {
//      val photoString = PhotosUtils.photoJsonString()
//      val photos = PhotosUtils.photoUrlsFromJsonString(photoString ?: "")
//
//      if (photos != null) {
//        photosLiveData.postValue(photos)
//      }
//    }
//    val thread = Thread(runnable)
//    thread.start()
//
//  }

  override fun getBanner(): LiveData<String> {
    //    fetchBannerData()

    FetchBannerAsyncTask(callBack = { banner ->
      bannerLiveData.value = banner
    }).execute()
    return bannerLiveData
  }

//  private fun scheduleFetchJob() {
//    val jobScheduler = RWDC2018Application.getAppContext()
//      .getSystemService(Service.JOB_SCHEDULER_SERVICE) as JobScheduler
//    val jobInfo = JobInfo.Builder(1000,
//      ComponentName(RWDC2018Application.getAppContext(), PhotosJobService::class.java))
//      .setPeriodic(900000) // 15 minutes in millisec
//      .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//      .build()
//
//    jobScheduler.schedule(jobInfo)
//
//  }
//
//  private fun scheduleLogJob() {
//    val jobScheduler = RWDC2018Application.getAppContext()
//      .getSystemService(Service.JOB_SCHEDULER_SERVICE) as JobScheduler
//
//    val logJobInfo = JobInfo.Builder(
//      1001,
//      ComponentName(RWDC2018Application.getAppContext(), LogJobService::class.java))
//      .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//      .build()
//
//    jobScheduler.schedule(logJobInfo)
//
//  }

  private fun schedulePeriodicWork() {

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .setRequiresStorageNotLow(true)
      .build()

    val request = PeriodicWorkRequest.Builder(DownloadWorker::class.java, 15, TimeUnit.MINUTES)
      .setConstraints(constraints)
      .addTag(PHOTO_DOWNLOAD_TAG)
      .build()

    val workManager = WorkManager.getInstance()

    workManager.cancelAllWorkByTag(PHOTO_DOWNLOAD_TAG)
    workManager.enqueue(request)

  }

//  private fun fetchBannerData() {
//
//    val runnableBanner = Runnable {
//      val bannerString = PhotosUtils.photoJsonString()
//      val banner= PhotosUtils.bannerFromJsonString(bannerString ?: "")
//
//      if (banner != null) {
//        bannerLiveData.postValue(banner)
//      }
//    }
//
//    // run the new thread concurrently
//    val bannerThread = Thread(runnableBanner)
//    bannerThread.start()
//
//  }

  class FetchPhotoAsyncTask(val callBack: (List<String>) -> Unit) : AsyncTask<Void, Void, List<String>>() {
    override fun doInBackground(vararg params: Void?): List<String>? {
      val photoString = PhotosUtils.photoJsonString()
      val photos = PhotosUtils.photoUrlsFromJsonString(photoString ?: "")
      return photos
    }

    override fun onPostExecute(result: List<String>?) {
      if (result != null) {
        callBack(result)
      }
    }
  }

  class FetchBannerAsyncTask(val callBack: (String) -> Unit) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
      val bannerString = PhotosUtils.photoJsonString()
      val banner= PhotosUtils.bannerFromJsonString(bannerString ?: "")
      return banner
    }

    override fun onPostExecute(result: String?) {
      if (result != null) {
        callBack(result)
      }
    }
  }

}