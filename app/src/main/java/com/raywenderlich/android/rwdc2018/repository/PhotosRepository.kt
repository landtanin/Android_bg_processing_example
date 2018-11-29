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
import android.os.AsyncTask
import com.raywenderlich.android.rwdc2018.app.PhotosUtils

class PhotosRepository : Repository {
  private val photosLiveData = MutableLiveData<List<String>>()
  private val bannerLiveData = MutableLiveData<String>()

  override fun getPhotos(): LiveData<List<String>> {
//    fetchPhotoData()

    FetchPhotoAsyncTask { photos ->
      photosLiveData.value = photos
    }.execute()
    return photosLiveData
  }

  private fun fetchPhotoData() {

    val runnable = Runnable {
      val photoString = PhotosUtils.photoJsonString()
      val photos = PhotosUtils.photoUrlsFromJsonString(photoString ?: "")

      if (photos != null) {
        photosLiveData.postValue(photos)
      }
    }
    val thread = Thread(runnable)
    thread.start()

  }

  override fun getBanner(): LiveData<String> {
    //    fetchBannerData()

    FetchBannerAsyncTask(callBack = { banner ->
      bannerLiveData.value = banner
    }).execute()
    return bannerLiveData
  }

  private fun fetchBannerData() {

    val runnableBanner = Runnable {
      val bannerString = PhotosUtils.photoJsonString()
      val banner= PhotosUtils.bannerFromJsonString(bannerString ?: "")

      if (banner != null) {
        bannerLiveData.postValue(banner)
      }
    }

    // run the new thread concurrently
    val bannerThread = Thread(runnableBanner)
    bannerThread.start()

  }

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