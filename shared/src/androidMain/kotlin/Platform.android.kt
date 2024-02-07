import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun takePictureNativeView(imageHandler: ImageHandler, redraw: Int) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context), context.packageName + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            capturedImageUri = uri
            if (capturedImageUri.path?.isNotEmpty() == true) {
                val imageBitmap = loadImageBitmap(capturedImageUri, context)
                imageBitmap?.let {
                    imageHandler.onImageBitmapCaptured(imageBitmap.asImageBitmap())
                }
            }
        } else {
            imageHandler.onCancelled()
        }

    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(redraw) {

            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(uri)
            } else {
                // Request a permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

fun loadImageBitmap(imageUri: Uri, context: Context): Bitmap? {
    var bitmap: Bitmap? = null

    // Load bitmap in a side-effect

    bitmap = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
        ImageDecoder.decodeBitmap(source)
    }
    return bitmap
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}

@Composable
fun MainView() = CrackDetailScreen()