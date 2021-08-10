package com.flickr.demo.DataAccess

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.flickr.demo.Model.ImageModel
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class RequestData(context: Context) {
    private val TAG: String = "RequestData"

    private val gson: Gson = Gson()
    private var queue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        fun getInstance(context: Context) : RequestData {
            return RequestData(context)
        }
    }

    fun getImages(tags: String, numberOfImages: Int, response: (images: ArrayList<ImageModel>, errorMessage: String) -> Unit) {
        // Use Uri.Builder class instead of JSONObject Object for params
        val urlBuilder = Uri.parse(MAIN_URL + GET_IMAGES).buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("method", "flickr.photos.search")
            .appendQueryParameter("tags", tags)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", true.toString())
            .appendQueryParameter("extras", "media,url_sq,url_m")
            .appendQueryParameter("per_page", numberOfImages.toString())
            .appendQueryParameter("page", 1.toString())
            .build().toString()

        Log.d(TAG, "getImages: $urlBuilder")
        val outputImages = ArrayList<ImageModel>()
        // Start API Call
        val myReq = StringRequest(Request.Method.POST, urlBuilder, {
            res -> // Success API Call
            Log.d(TAG, "getImages: $res")

            val responseObject = JSONObject(res)
            val status = responseObject.optString("stat") ?: "not okay"
            // Check Status equals "ok" & the response contains "photos"
            if (status.contentEquals("ok") && responseObject.has("photos")) {
                val photos = responseObject.optJSONObject("photos") ?: JSONObject()
                val images = photos.optJSONArray("photo") ?: JSONArray()

                // Convert & Loop the JSON Array of "photo" into ImageModel Class
                for (i in 0 until images.length()) {
                    val item = images.getJSONObject(i)
                    val image = gson.fromJson(item.toString(), ImageModel::class.java)
                    outputImages.add(image)
                }

                response(outputImages, "")
                return@StringRequest
            }

            // Show empty result for Code 3
            val code = responseObject.optInt("code", -1)
            if (code == 3) {
                response(outputImages, "")
                return@StringRequest
            }

            response(outputImages, "100-Response Unable to Process Response")
        }, {
            VolleyError -> // Fail API Call
            Log.e(TAG, "getImages: ", VolleyError)
            response(outputImages, "501-Error API Call")
        })

        queue.add(myReq)
    }
}