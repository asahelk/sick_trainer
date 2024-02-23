package utils

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateFrontBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
        postScale(-1f, 1f)
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
        postScale(1f, 1f)
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}