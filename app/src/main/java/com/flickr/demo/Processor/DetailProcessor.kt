package com.flickr.demo.Processor

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.flickr.demo.Model.ImageModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class DetailProcessor(): ViewModel() {
    private var mInputImage: ImageModel? = null
    private var mInputBitmap: Bitmap? = null

    // Once ImageModel is set, load the image and obtained the Bitmap value for save purpose
    fun setSelectedImage(activity: Activity, imageModel: ImageModel) {
        mInputImage = imageModel
        viewModelScope.launch {
            if (mInputImage != null) {
                val imageLoader = ImageLoader(activity)
                val request = ImageRequest.Builder(activity)
                    .data(mInputImage!!.BigImageURL)
                    .build()
                val drawable = imageLoader.execute(request).drawable
                mInputBitmap = (drawable as BitmapDrawable).bitmap
            }
        }
    }

    fun getImage(): ImageModel? {
        return mInputImage
    }

    // Save the obtained Bitmap Value into Gallery
    fun saveImage(activity: Activity) {
        if (mInputBitmap != null)
            saveToGallery(activity, bitmap = mInputBitmap!!, albumName = "FlickrAlbum")
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        // Save the image as currentTimeMillis() file name
        val filename = "${System.currentTimeMillis()}.png"
        // Compress the Image for Output Stream
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        // Check Android Version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Save it to Media Store for Android Version Q and above
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$albumName")
            }

            context.contentResolver.let {
                it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }
        } else {
            // Save it to External Environment for Android Version Q below
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            write(FileOutputStream(image))
        }
    }
}