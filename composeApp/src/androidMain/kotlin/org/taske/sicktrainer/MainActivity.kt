package org.taske.sicktrainer

import App
import CrackDetailScreen
import Render
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CrackDetailScreen()
        }
    }
}

//@Preview
//@Composable
//fun CrackAndroidPreview() {
//    CrackDetailScreen()
//}
//
//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}
//
//@Preview
//@Composable
//fun TestPrev() {
//    Render()
//}

//@Preview
//@Composable
//fun Test2() {
//    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues: PaddingValues ->
//        AndroidView(modifier = Modifier
//            .fillMaxSize()
//            .padding(paddingValues), factory = { context ->
//            PreviewView(context).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//                setBackgroundColor(Color.BLUE)
//                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
//                scaleType = PreviewView.ScaleType.FILL_START
//            }.also { previewView ->
//
//            }
//        })
//    }
//}