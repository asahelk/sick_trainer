import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun CrackDetailScreen() {

    val viewModel = getViewModel(Unit, viewModelFactory { CameraViewViewModel() })

    val showCamera by viewModel.showCameraView.collectAsState()

    var buttonClick = remember {
        mutableStateOf(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            viewModel.showCameraView()
            buttonClick.value++
        }) {
            Text("Enable camera")
        }
        if (showCamera) {
            CameraScreen(viewModel, buttonClick.value)
        }
        MyImageDisplay(viewModel)
    }

}

@Composable
fun CameraScreen(viewModel: CameraViewViewModel, buttonClick: Int) {

    Column(modifier = Modifier.height(300.dp).fillMaxWidth()) {
        //Overlay content here
//        TakePictureNativeView(viewModel, buttonClick)
        CameraContent(viewModel)
    }
}

@Composable
fun MyImageDisplay(viewModel: CameraViewViewModel) {
    val imageBytes by viewModel.imageBytes.collectAsState()


    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageBytes?.let {
            Text("Image Received")
            Image(
                bitmap = it,
                contentDescription = null
            )
        }

    }

}

@Composable
expect fun TakePictureNativeView(imageHandler: ImageHandler, redraw: Int = 0)

@Composable
expect fun CameraContent(imageHandler: ImageHandler)