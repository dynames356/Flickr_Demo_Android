package com.flickr.demo.Misc

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class Utilities {
    companion object {
        val PERMISSIONS: ArrayList<String> = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )

        // Permission for Internat, Media Location, & Read/Write Storage function
        fun getMissingPermissions(activity: Activity) : Array<String> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                PERMISSIONS.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
            else
                PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            val requestedPermissions = ArrayList<String>()
            for (permission in PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                    requestedPermissions.add(permission)
            }

            return requestedPermissions.toTypedArray()
        }
    }
}