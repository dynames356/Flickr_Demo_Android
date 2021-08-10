package com.flickr.demo.Activity

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.flickr.demo.Activity.ui.theme.Flick_DemoTheme
import com.flickr.demo.Model.ImageModel
import com.flickr.demo.Processor.DetailProcessor

class DetailActivity : ComponentActivity() {
    var mProcessor = DetailProcessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialised DetailProcessor when View is created
        mProcessor.setSelectedImage(this, intent.getSerializableExtra("InputImage") as ImageModel)
        setupUI()
    }

    fun setupUI() {
        setContent {
            Flick_DemoTheme {
                Surface(color = MaterialTheme.colors.background) {
                    InitialUI()
                }
            }
        }
    }

    @Composable
    fun InitialUI() {
        Scaffold(modifier = Modifier.padding(all = 10.dp)) {
            Column {
                InitTopBar()
                Spacer(modifier = Modifier.height(10.dp))
                ImageViewer()
            }
        }
    }

    // Initialised Top Bar UI with Save Button
    @Composable
    fun InitTopBar() {
        TopAppBar(
            title = {
                Text(
                    text = "Flickr Photos",
                    color = Color.White
                )
            },
            actions = {
                Button(
                    onClick = {
                        // Save the Image when Save Button is clicked
                        mProcessor.saveImage(this@DetailActivity)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    modifier = Modifier.padding(all = Dp(5f)),
                    border = null
                )
                {
                    Text(text = "Save")
                }
            },
            backgroundColor = colorResource(id = R.color.darker_gray),
            contentColor = Color.White,
            elevation = 12.dp
        )
    }

    // Image View Detail UI
    @Composable
    fun ImageViewer() {
        val inputImage = mProcessor.getImage()
        if (inputImage != null) {
            Image(
                // Load Big Image URL with Loading Icon as Placeholder while the image loads
                painter = rememberImagePainter(
                    data = inputImage.BigImageURL,
                    builder = {
                        crossfade(true)
                        placeholder(com.flickr.demo.R.drawable.loading_icon)
                    }
                ),
                contentDescription = inputImage.ImageDesc,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Flick_DemoTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colors.background) {
                InitialUI()
            }
        }
    }
}