package org.taske.sicktrainer

import MainView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainView()
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