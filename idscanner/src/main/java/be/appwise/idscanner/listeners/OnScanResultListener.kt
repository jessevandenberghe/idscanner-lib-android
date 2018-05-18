package be.appwise.idscanner.listeners

import android.graphics.Bitmap
import be.appwise.idscanner.models.ScanResult

interface OnScanResultListener {
	fun onScanResult(scan: ScanResult, bitmap: Bitmap)
}