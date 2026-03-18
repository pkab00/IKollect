package com.vbshkn.ikollect.data.service

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class BarcodeScannerService(context: Context) {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
        .build()
    private val scanner = GmsBarcodeScanning.getClient(context, options)

    fun startScanning(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
        onCanceled: () -> Unit
    ) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let { onSuccess(it) }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

            .addOnCanceledListener {
                onCanceled()
            }
    }
}