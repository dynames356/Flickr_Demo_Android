package com.flickr.demo

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    init {
        mApplication = this
    }

    companion object {
        private var mApplication : MainApplication? = null

        // Static function to get Application Context for VolleyRequest API Call
        fun getContext() : Context {
            return mApplication!!.applicationContext
        }
    }

}