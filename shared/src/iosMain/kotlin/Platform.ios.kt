import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.skia.Image
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

lateinit var uiFactory: () -> UIView
lateinit var myImageHandler: ImageHandler

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun TakePictureNativeView(imageHandler: ImageHandler, redraw: Int) {
    myImageHandler = imageHandler
    UIKitView(
        factory = uiFactory,
        modifier = Modifier.wrapContentSize()
            .border(2.dp, androidx.compose.ui.graphics.Color.Blue),
    )
}

@Composable
actual fun CameraContent(imageHandler: ImageHandler) {

}

fun passInByteArray(byteArray: ByteArray) {
    val imageBitmap = Image.makeFromEncoded(byteArray).toComposeImageBitmap()
    myImageHandler.onImageBitmapCaptured(imageBitmap)
}

fun onCameraCancelled() {
    myImageHandler.onCancelled()
}


@OptIn(ExperimentalForeignApi::class)
fun MainViewController(createCameraView: () -> UIView): UIViewController =
    ComposeUIViewController {
        uiFactory = createCameraView
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CrackDetailScreen()
            //CrackCaptureEntryScreen()
            Text("How to use SwiftUI inside Compose Multiplatform")
        }
    }

