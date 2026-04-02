package com.vbshkn.ikollect.presentation.feature.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.Bitmap
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.CameraPreview
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import java.io.File
import java.io.FileOutputStream
import kotlin.apply

@Composable
fun PhotocardCameraScreen(
    onPhotoTaken: (String?) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            IconButton(
                onClick = {
                    isLoading = true
                    takePhoto(
                        controller = controller,
                        context = context,
                        onPhotoTaken = { photo ->
                            onPhotoTaken(photo)
                            isLoading = false
                        }
                    )
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraPreview(controller, Modifier.fillMaxSize())
            Overlay()
        }
    }
    if (isLoading) {
        LoadingOverlay()
    }
}

@Composable
private fun Overlay() {
    val widthDp = 250.dp
    val heightDp = widthDp * 1.59f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cornerRadiusPx = 12.dp.toPx()

        val rectWidth = widthDp.toPx()
        val rectHeight = heightDp.toPx()
        val left = (size.width - rectWidth) / 2
        val top = (size.height - rectHeight) / 2

        val rectPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(offset = Offset(left, top), size = Size(rectWidth, rectHeight)),
                    cornerRadius = CornerRadius(cornerRadiusPx)
                )
            )
        }
        clipPath(rectPath, clipOp = ClipOp.Difference) {
            drawRect(Color.Black.copy(alpha = 0.7f))
        }
    }
}

private fun takePhoto(
    controller: LifecycleCameraController,
    context: Context,
    onPhotoTaken: (String?) -> Unit
) {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")

    val displayMetrics = context.resources.displayMetrics
    val displayHeight = displayMetrics.heightPixels.toFloat()
    val displayWidth = displayMetrics.widthPixels.toFloat()

    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val savedFile = processImage(
                    image = image,
                    outputFile = file,
                    density = context.resources.displayMetrics.density,
                    screenHeight = displayHeight,
                    screenWidth = displayWidth
                )
                Log.d("AlbumCameraScreen", savedFile?.absolutePath ?: "NULL")
                onPhotoTaken(savedFile?.absolutePath)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Error: ${exception.message}")
                onPhotoTaken(null)
            }
        }
    )
}

private fun processImage(
    image: ImageProxy,
    outputFile: File,
    density: Float,
    screenHeight: Float,
    screenWidth: Float
): File? {
    // 1. Извлекаем битмап с учётом угла поворота изображения
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rotation = image.imageInfo.rotationDegrees
    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
    val rotatedBitmap = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
    image.close()

    // 2. Вычисляем размеры и координаты обработанного изображения
    val zoneWidthDp = 250f
    val zoneHeightDp = zoneWidthDp * 1.59f // Размеры рамки в DP
    val screenHeightDp = screenHeight / density
    val screenWidthDp = screenWidth / density // Размеры экрана устройства в DP
    val bitmapHeight = rotatedBitmap.height.toFloat()
    val bitmapWidth = rotatedBitmap.width.toFloat() // Размеры битмапа

    val scale = maxOf(screenHeight / bitmapHeight, screenWidth / bitmapWidth)
    val visibleHeight = screenHeight / scale
    val visibleWidth = screenWidth / scale // Видимая часть картинки без системного скейла

    // Конечные координаты обрезанной части
    val cropEndX = ((zoneWidthDp / screenWidthDp) * visibleWidth).toInt()
    val cropEndY = ((zoneHeightDp / screenHeightDp) * visibleHeight).toInt()
    // Начальные координаты обрезанной части
    val cropStartX = ((bitmapWidth - cropEndX) / 2).toInt().coerceAtLeast(0)
    val cropStartY = ((bitmapHeight - cropEndY) / 2).toInt().coerceAtLeast(0)

    // 3. Применяем изменения к изображению и сохраняем в файл
    return try {
        val croppedBitmap = android.graphics.Bitmap.createBitmap(
            rotatedBitmap, cropStartX, cropStartY, cropEndX, cropEndY
        )
        FileOutputStream(outputFile).use { out ->
            croppedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, out)
        }
        rotatedBitmap.recycle()
        croppedBitmap.recycle()
        outputFile
    } catch (e: Exception) {
        Log.d("AlbumCameraScreen", "Error while processing the file: ${e.message}")
        return null
    }
}