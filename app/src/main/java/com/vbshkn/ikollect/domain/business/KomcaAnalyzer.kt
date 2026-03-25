package com.vbshkn.ikollect.domain.business

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class KomcaAnalyzer(
    private val onNumberDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    extractKomcaNumber(visionText.text)
                        ?.let { onNumberDetected(it) }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun extractKomcaNumber(text: String): String? {
        val regex = Regex("""[0-9]{8,12}""")
        val match = regex.find(text)
        return match?.groups?.get(0)?.value
    }
}