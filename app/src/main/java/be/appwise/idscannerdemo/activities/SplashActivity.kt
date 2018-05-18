package be.appwise.idscannerdemo.activities

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import be.appwise.idscanner.services.IdScannerService
import be.appwise.idscannerdemo.R

class SplashActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		
		val i = Intent(this, MainActivity::class.java)
		
		class InitOCRAsync : AsyncTask<Unit, Unit, Unit>() {
			override fun onPostExecute(result: Unit?) {
				startActivity(i)
				finish()
				
			}
			
			override fun doInBackground(vararg p0: Unit?) {
				IdScannerService.init(applicationContext)
				Log.d("blub", "doInBackground: " + applicationContext.filesDir.absolutePath)
			}
		}
		InitOCRAsync().execute()
	}
}
