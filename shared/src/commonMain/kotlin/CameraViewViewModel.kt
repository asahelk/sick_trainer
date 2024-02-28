import androidx.compose.ui.graphics.ImageBitmap
import com.seiko.imageloader.Bitmap
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class CameraViewViewModel: ViewModel(), ImageHandler {
    private val _imageBytes = MutableStateFlow<ImageBitmap?>(null)
    val imageBytes: StateFlow<ImageBitmap?> = _imageBytes

    private val _showCameraView = MutableStateFlow<Boolean>(false)
    val showCameraView: StateFlow<Boolean> = _showCameraView

    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    override fun onImageBytesCaptured(byteArray: ByteArray?) {
        println("Image byteArray is captured size: ${byteArray?.size}")
        _showCameraView.value = false
    }

    override fun onImageBitmapCaptured(bitmap: ImageBitmap) {
        _imageBytes.value = bitmap
        println("bitmap captured")
    }

    override fun onCancelled() {
        _showCameraView.value = false
        println("Camera view was closed")
    }

    fun showCameraView(){
        _showCameraView.value = true
    }

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
    }

}