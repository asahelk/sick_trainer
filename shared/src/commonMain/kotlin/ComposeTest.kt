import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun Render() {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
//            viewModel.showCameraView()
//            buttonClick.value++
        }) {
            Text("Capture photo")
        }
//        if (showCamera) {
//            CameraScreen(viewModel, buttonClick.value)
//        }
    }
}