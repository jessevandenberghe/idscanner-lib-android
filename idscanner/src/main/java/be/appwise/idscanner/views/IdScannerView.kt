package be.appwise.idscanner.views

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import be.appwise.idscanner.helpers.CharacterConverterHelper
import be.appwise.idscanner.helpers.convertYuvToBitmap
import be.appwise.idscanner.listeners.OnScanResultListener
import be.appwise.idscanner.models.IDScan
import be.appwise.idscanner.models.ScanResult
import be.appwise.idscanner.services.TesseractOCRService
import com.otaliastudios.cameraview.CameraView
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import android.util.DisplayMetrics



class IdScannerView(context: Context, attrs: AttributeSet?) : CameraView(context, attrs) {
	
	private var isProcessing = false
	private var isScanning = false
	private var isRendering = false
	
	fun setOnResultListener(listener: OnScanResultListener){
		super.addFrameProcessor{
			if (!isProcessing && isScanning && !isRendering && it.data != null) {
				val freezedFrame = it
				val freezedFrameData = it.data
				isRendering = true
				
				val renderedBitmap = convertYuvToBitmap(freezedFrameData, freezedFrame)
				val heightToPixels = convertDpToPixel(100).toInt()
				val vMargin = (renderedBitmap!!.height - heightToPixels)/2
				val scaledBitmap = Bitmap.createBitmap(renderedBitmap, 100, vMargin, renderedBitmap.width - 200, heightToPixels)
				val binarizedBitmap = TesseractOCRService.binarizeImage(scaledBitmap)
				
				processBitmap(binarizedBitmap, listener)
			}
		}
	}
	
	private fun convertDpToPixel(dp: Int): Float {
		val metrics = Resources.getSystem().displayMetrics
		val px = dp * (metrics.densityDpi / 160f)
		return Math.round(px).toFloat()
	}
	
	private fun processBitmap(bitmap: Bitmap?, listener: OnScanResultListener){
		bitmap?: return
		
		val resultFound = { data: String ->
			val activity = context as Activity
			activity.runOnUiThread {
				if (isScanning) {
					Log.d("Scanning", "processBitmap: " + data)
					val identity = CharacterConverterHelper.convertToId(data)
					if (identity != null) {
						val scanResult = ScanResult(identity, data)
						listener.onScanResult(scanResult, bitmap)
					}
					else {
						val scanResult = ScanResult(IDScan(), data)
						listener.onScanResult(scanResult, bitmap)
					}
				}
				isProcessing = false
				isRendering = false
			}
		}
		
		val errorUi = { e: Throwable ->
			Log.e("error", "processBitmap: " , e)
			isProcessing = false
			isRendering = false
		}
		
		try {
			if (!isProcessing) {
				isProcessing = true
				Flowable.fromCallable({
					CharacterConverterHelper.convertIdCode(TesseractOCRService.getOCRResult(bitmap))
				})
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.computation())
						//.timeout(3, TimeUnit.SECONDS)
						.subscribe(resultFound, errorUi)
			}
		} catch (e: Exception) {
			Log.e("Scanning", "processBitmap: " , e)
		}
	}
	
	fun startScanning(){
		isScanning = true
	}
	
	fun stopScanning(){
		isScanning = false
	}
	
	override fun start() {
		super.start()
		Log.d("IdScanView", "start: ")
	}
	
	override fun stop() {
		super.stop()
		Log.d("IdScanView", "stop: ")
	}
	
	override fun destroy() {
		super.destroy()
		Log.d("IdScanView", "destroy: ")
	}
}