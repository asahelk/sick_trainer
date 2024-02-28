import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.rotateFrontBitmap
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun TakePictureNativeView(imageHandler: ImageHandler, redraw: Int) {
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
//            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
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

@Composable
fun rememberRotationListener(context: Context): OrientationEventListener {
    return remember(context) {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }
                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_90
                    in 135 until 225 -> Surface.ROTATION_0
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                Log.e("ROTTTT","xvd "+rotation)
                // Do something with the rotation, e.g., update CameraX preview
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraContent(imageHandler: ImageHandler, typeButtonClicked: Int) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutineScope = rememberCoroutineScope()

    val rotationListener = rememberRotationListener(context)

    // Start listening for orientation changes when the composable is first composed
    rotationListener.enable()

    // Remember to disable the listener when the composable is removed from the composition
    DisposableEffect(Unit) {
        onDispose {
            rotationListener.disable()
        }
    }

    var imageCapture: ImageCapture? = null

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val cameraExecutor: ExecutorService = remember {
        Executors.newSingleThreadExecutor()
    }

    val handler = Handler(Looper.getMainLooper())

    val cameraPermissionState: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {

        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()

        Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues: PaddingValues ->

            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                    factory = { context ->
                        val previewView = PreviewView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            setBackgroundColor(Color.BLACK)
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            scaleType = PreviewView.ScaleType.FIT_CENTER

                        }

                        previewView
                    },
                    update = { previewView ->

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                        cameraProvider.unbindAll()

                        when (typeButtonClicked) {
                            0 -> {
                                previewView.controller = cameraController
                                cameraController.unbind()
                                cameraController.bindToLifecycle(lifecycleOwner)
                                cameraController.cameraSelector =
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                cameraController.imageCaptureMode =
                                    ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY

//                                coroutineScope.launch { //Give me error: camera not Initialized
//                                    while (true) {
//                                        if(cameraController.cameraInfo?.cameraState?.isInitialized !== null){
//                                            capturePhoto1(
//                                                context, cameraController, imageHandler
//                                            )
//                                            delay(5000)
//                                        }
//
//                                    }
//                                }

                                val captureTask = runnable {
                                    capturePhoto1(
                                        context, cameraController, imageHandler
                                    )
                                    handler.postDelayed(this, 5000)
                                }
                                handler.post(captureTask)
                            }

                            1 -> {

                                Log.e("ROTATION!", "->" + previewView.rotation)

                                cameraProviderFuture.addListener({

                                    val preview = Preview.Builder().build().apply {
                                        setSurfaceProvider(previewView.surfaceProvider)
                                        targetRotation = Surface.ROTATION_0
                                    }

                                    imageCapture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                        .setJpegQuality(100)
//                                        .setTargetRotation(Surface.ROTATION_90)
                                        .build()


                                    bindCameraUseCases(
                                        cameraProvider,
                                        lifecycleOwner,
                                        cameraExecutor,
                                        preview,
                                        previewView,
                                        imageCapture
                                    )
                                }, ContextCompat.getMainExecutor(context))

                                coroutineScope.launch {
                                    while (true) {
                                        capturePhoto2(
                                            context, imageHandler, imageCapture, cameraExecutor
                                        )
                                        delay(5000)
                                    }
                                }

//                                val captureTask = runnable {
//                                    capturePhoto2(
//                                        context, imageHandler, imageCapture, cameraExecutor
//                                    )
//                                    handler.postDelayed(this, 5000)
//
//                                }
//                                handler.post(captureTask)
                            }
                        }


                    })

            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(32.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
                ) {

                    Button(modifier = Modifier.height(60.dp),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            when (typeButtonClicked) {
                                0 -> {
                                    capturePhoto1(
                                        context, cameraController, imageHandler
                                    )
                                }

                                1 -> {
                                    capturePhoto2(
                                        context, imageHandler, imageCapture, cameraExecutor
                                    )
                                }
                            }

                        }) {
                        Text(text = "Take Photo", fontSize = 12.sp)
                    }
                }
            }
        }
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}

inline fun runnable(crossinline body: Runnable.() -> Unit) = object : Runnable {
    override fun run() = this.body()
}

//inline fun View.afterMeasured(crossinline block: () -> Unit) {
//    if (measuredWidth > 0 && measuredHeight > 0) {
//        block()
//    } else {
//        viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                if (measuredWidth > 0 && measuredHeight > 0) {
//                    viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    block()
//                }
//            }
//        })
//    }
//}

@SuppressLint("ClickableViewAccessibility")
private fun bindCameraUseCases(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: ExecutorService,
    preview: Preview,
    previewView: PreviewView,
    imageCapture: ImageCapture?
) {

    val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
        ResolutionStrategy(
            Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_NONE
        )
    ).build()

    val imageAnalyzer = ImageAnalysis.Builder().setResolutionSelector(resolutionSelector)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
            it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                // Your image analysis logic  here
                image.close()
            })
        }

    imageAnalyzer.targetRotation = Surface.ROTATION_90

    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    try {
//        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalyzer, imageCapture
        )

//        previewView.afterMeasured {
//            previewView.setOnTouchListener { _, event ->
//                return@setOnTouchListener when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        true
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
//                            previewView.width.toFloat(), previewView.height.toFloat()
//                        )
//                        val autoFocusPoint = factory.createPoint(event.x, event.y)
//                        try {
//                            camera.cameraControl.startFocusAndMetering(
//                                FocusMeteringAction.Builder(
//                                    autoFocusPoint,
//                                    FocusMeteringAction.FLAG_AF
//                                ).apply {
//                                    //focus only when the user tap the preview
//                                    disableAutoCancel()
//                                }.build()
//                            )
//                        } catch (e: CameraInfoUnavailableException) {
//                            Log.d("ERROR", "cannot access camera", e)
//                        }
//                        true
//                    }
//                    else -> false // Unhandled event.
//                }
//            }
//        }


    } catch (exc: Exception) {
        // Handle any errors
        Log.e("ERRROR", exc.message, exc)
//                                cameraProvider.unbindAll()
        cameraExecutor.shutdown()
    }
}

private fun capturePhoto2(
    context: Context,
    imageHandler: ImageHandler,
    imageCapture: ImageCapture?,
    cameraExecutorService: ExecutorService
) {


    imageCapture?.takePicture(cameraExecutorService,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val correctedBitmap: Bitmap =
                    image.toBitmap().rotateFrontBitmap(image.imageInfo.rotationDegrees)

                Log.e("ROTATION", "ROTATION!" + image.imageInfo.rotationDegrees)

                Log.e("ORIENTATION!", "ORIENTATION!" + image.imageInfo.rotationDegrees)

//            onPhotoCaptured(correctedBitmap)
                imageHandler.onImageBitmapCaptured(correctedBitmap.asImageBitmap())
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                // Image capture error handling
                Log.e("ERRROR", exception.message, exception)
            }
        })

    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)


}

private fun capturePhoto1(
    context: Context,
    cameraController: LifecycleCameraController,
    imageHandler: ImageHandler,
) {

    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val correctedBitmap: Bitmap =
                image.toBitmap().rotateFrontBitmap(image.imageInfo.rotationDegrees)

            Log.e("ROTATION", "ROTATION!" + image.imageInfo.rotationDegrees)
//            onPhotoCaptured(correctedBitmap)
            imageHandler.onImageBitmapCaptured(correctedBitmap.asImageBitmap())
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraContent", "Error capturing image", exception)
        }
    })
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

@Composable fun MainView() = App()