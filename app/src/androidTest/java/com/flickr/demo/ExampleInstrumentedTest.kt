package com.flickr.demo

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.flickr.demo.DataAccess.RequestData
import com.flickr.demo.Model.ImageModel
import org.json.JSONArray

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val signal = CountDownLatch(1)


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.flickr.demo", appContext.packageName)
    }

    @Test
    fun getImageAPICall() {
        var outputImages = ArrayList<ImageModel>()
        var outputMessage = ""

        RequestData.getInstance(InstrumentationRegistry.getInstrumentation().targetContext).getImages("Electrolux", 21) {
                images, errorMessage ->
            outputImages = images
            outputMessage = errorMessage
        }

        signal.await(5, TimeUnit.SECONDS)
        assert(outputImages.size > 0 && outputMessage.isEmpty())
    }
}