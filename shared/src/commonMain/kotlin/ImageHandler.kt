import androidx.compose.ui.graphics.ImageBitmap

interface ImageHandler{
    fun onImageBytesCaptured(byteArray: ByteArray?)
    fun onImageBitmapCaptured(bitmap: ImageBitmap)
    fun onCancelled()
}