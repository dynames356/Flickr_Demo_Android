package com.flickr.demo.Processor

import android.app.Activity
import android.content.Intent
import com.flickr.demo.Activity.DetailActivity
import com.flickr.demo.DataAccess.RequestData
import com.flickr.demo.Listener.ImageListListener
import com.flickr.demo.MainApplication
import com.flickr.demo.Model.ImageModel

class MainProcessor(val imageListener: ImageListListener) {
    private var mImageList = ArrayList<ImageModel>()
    private var mNumbOfImages: Int = 21

    fun loadImage(tags: String) {
        // API Call for Image Listing
        RequestData.getInstance(MainApplication.getContext()).getImages(tags, numberOfImages = mNumbOfImages) {
                images: ArrayList<ImageModel>, errorMessage: String ->
            mImageList = images
            imageListener.imageList(errorMessage = errorMessage)
        }
    }

    fun getImageList(): ArrayList<ImageModel> {
        return mImageList
    }

    fun navigateToDetail(activity: Activity, inputImage: ImageModel) {
        // Create Intent & Navigate the MainActivity to DetailActivity when the Grid Cell is pressed
        val detailIntent = Intent(activity, DetailActivity::class.java)
        detailIntent.putExtra("InputImage", inputImage)
        activity.startActivity(detailIntent)
    }
}