package com.hobby_projects.myphotopicker

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hobby_projects.myphotopicker.ui.theme.MyPhotoPickerTheme
import java.lang.Math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPhotoPickerTheme {
                // A surface container using the 'background' color from the theme
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var index = 0
    var btnsEnabled by remember { mutableStateOf(false) }
    var direction by remember { mutableStateOf(-1) }
    val context = LocalContext.current

    var singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedUri = uri
            index = 0
            btnsEnabled = false
        }
    )
    var multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            selectedImageUris = uris
            selectedUri = uris[0]
            index = 0
            if (uris.size > 1) btnsEnabled = true
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Text(text = "Pick a Photo")
                }
                Button(onClick = {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Text(text = "Pick Multiple Photos")
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                    enabled = btnsEnabled && (selectedImageUris.isNotEmpty() && index > 0),
                    onClick = {
                        if (selectedImageUris.isNotEmpty() && index > 0) {
                            selectedUri = selectedImageUris[--index]
                        }
                    }) {
                    Icon(Icons.Default.KeyboardArrowLeft, null)
                }

                Box(
                    contentAlignment  = Alignment.Center,
                    modifier = Modifier
                    .weight(1f)
                    .background(Color(211, 211, 211, 255))
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()

                                val (x, y) = dragAmount
                                if (kotlin.math.abs(x) > kotlin.math.abs(y)) {
                                    when {
                                        x > 0 -> {
                                            //right
                                            direction = 0
                                        }
                                        x < 0 -> {
                                            // left
                                            direction = 1
                                        }
                                    }
                                } else {
                                    when {
                                        y > 0 -> {
                                            // down
                                            direction = 2
                                        }
                                        y < 0 -> {
                                            // up
                                            direction = 3
                                        }
                                    }
                                }

                            },
                            onDragEnd = {
                                when (direction) {
                                    0 -> {
                                        if(btnsEnabled && (selectedImageUris.isNotEmpty() && index > 0)){
                                            Toast.makeText(context, "swipped right", Toast.LENGTH_SHORT).show()
                                            if (selectedImageUris.isNotEmpty() && index > 0) {
                                                selectedUri = selectedImageUris[--index]
                                            }
                                        }
                                    }
                                    1 -> {
                                        if(btnsEnabled && (selectedImageUris.isNotEmpty() && index < selectedImageUris.size - 1)){
                                            Toast.makeText(context, "swipped left", Toast.LENGTH_SHORT).show()
                                            if (selectedImageUris.isNotEmpty() && index < selectedImageUris.size - 1) {
                                                selectedUri = selectedImageUris[++index]
                                            }
                                        }
                                    }
                                    2 -> {
                                        Toast.makeText(context, "swipped down", Toast.LENGTH_SHORT).show()
                                    }
                                    3 -> {
                                        Toast.makeText(context, "swipped up", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                direction = -1
                            }
                        )
                    }
                ) {
                    if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = null,
                            modifier = Modifier.height(500.dp),
                            contentScale = ContentScale.FillHeight
                        )
                    } else {
                        Text(
                            modifier = Modifier,
                            text = "No Image to Display",
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Button(modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                    enabled = btnsEnabled && (selectedImageUris.isNotEmpty() && index < selectedImageUris.size - 1),
                    onClick = {
                        if (selectedImageUris.isNotEmpty() && index < selectedImageUris.size - 1) {
                            selectedUri = selectedImageUris[++index]
                        }
                    }) {
                    Icon(Icons.Default.KeyboardArrowRight, null)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyPhotoPickerTheme {
        MainApp()
    }
}

