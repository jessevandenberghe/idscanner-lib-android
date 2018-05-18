package be.appwise.idscannerdemo.activities

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import be.appwise.idscanner.listeners.OnScanResultListener
import be.appwise.idscanner.models.ScanResult
import be.appwise.idscanner.services.TesseractOCRService
import be.appwise.idscannerdemo.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		initView()
	}
	
	private fun initView() {
		setupScan()
		
		btn_scan.setOnClickListener {
			setupScan()
		}
	}
	
	private fun setupScan() {
		id_scanner_camera.setOnResultListener(object : OnScanResultListener {
			override fun onScanResult(scan: ScanResult, bitmap: Bitmap) {
				id_scanner_camera.stopScanning()
				tv_result.text = scan.rawData
				iv_image_result.setImageBitmap(bitmap)
			}
		})
		id_scanner_camera.startScanning()
	}
	
	override fun onResume() {
		super.onResume()
		id_scanner_camera.start()
	}
	
	override fun onPause() {
		super.onPause()
		id_scanner_camera.stop()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		id_scanner_camera?.destroy()
	}
}
