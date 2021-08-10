package com.flickr.demo.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.flickr.demo.Activity.ui.theme.Flick_DemoTheme
import com.flickr.demo.Listener.ImageListListener
import com.flickr.demo.Misc.Utilities
import com.flickr.demo.Model.ImageModel
import com.flickr.demo.Processor.MainProcessor
import com.flickr.demo.R

@ExperimentalFoundationApi
class MainActivity : ComponentActivity(), ImageListListener {
    var mProcessor = MainProcessor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        checkPermissions()
    }

    private fun checkPermissions() {
        // Check Permission First before Load Image
        val permissions = Utilities.getMissingPermissions(this)
        if (permissions.isNotEmpty()) {
            activityResultLauncher.launch(permissions)
            return
        }

        // Initial Load of Images
        mProcessor.loadImage("Electrolux")
    }

    // If User permit, proceed with API Call, otherwise check permission again
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val permitted = permissions.entries.all {
            it.value == true
        }

        if (permitted)
            mProcessor.loadImage("Electrolux")
        else
            checkPermissions()
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
                SearchUIBar()
                Spacer(modifier = Modifier.height(10.dp))
                ImageListUI()
            }
        }
    }

    // Initialised Top Bar UI
    @Composable
    fun InitTopBar() {
        TopAppBar(
            title = {
                Text(
                    text = "Flickr Photos",
                    color = Color.White
                )
            },
            backgroundColor = colorResource(id = android.R.color.darker_gray),
            contentColor = Color.White,
            elevation = 12.dp
        )
    }

    // Initialised Search Bar UI
    @Composable
    fun SearchUIBar() {
        // Check InputValue State of the Search Bar for API Call
        val inputValue = remember {
            mutableStateOf(TextFieldValue())
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            TextField(
                value = inputValue.value,
                onValueChange = {
                    inputValue.value = it
                    // Load Images if the Search Bar Value change
                    mProcessor.loadImage(inputValue.value.text)
                },
                placeholder = { Text(text = "Enter Tags") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                ),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif
                ),
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                )
            )
        }
    }

    //
    @Composable
    fun ImageListUI() {
        // Get List of ImageModel to populate the Grid
        val images: ArrayList<ImageModel> = mProcessor.getImageList()
        LazyVerticalGrid(
            cells = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            items(images.size) { index ->
                // Loop ImageModel object and generate the Grid Cell for each object
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ImageCell(image = images[index])
                }
            }
        }
    }

    // Populate each Grid Cell with this UI
    @Composable
    fun ImageCell(image: ImageModel) {
        Image(
            // Load Small Image URL with Loading Icon as Placeholder while the image loads
            painter = rememberImagePainter(
                data = image.SmallImageURL,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.loading_icon)
                }
            ),
            contentDescription = image.ImageDesc,
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .padding(vertical = 5.dp)
                .clickable {
                    // Navigate to Detail Page when the Grid Cell is clicked
                    mProcessor.navigateToDetail(this, image)
                }
        )
    }

    // Callback for Image Populated by the API Call
    override fun imageList(errorMessage: String) {
        if (errorMessage.isEmpty()) {
            // TODO: Should have better way than refreshing entire UI.....
            // Refresh the UI
            setupUI()
        } else {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Flick_DemoTheme {
            Surface(color = MaterialTheme.colors.background) {
                InitialUI()
            }
        }
    }
}