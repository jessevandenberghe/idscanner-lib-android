package be.appwise.idscanner.services

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import be.appwise.idscanner.exceptions.OcrServiceNotInitialisedException
import com.googlecode.leptonica.android.*
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream


class IdScannerService {
	companion object {
		private var datapath = ""
		private var mTess = TessBaseAPI()
		private var init = false
		
		private fun initTrainingFile(dataPath: String, assets: AssetManager) {
			this.datapath = dataPath
			val dir = File(dataPath + "/tessdata/")
			val file = File(dataPath + "/tessdata/" + "eng.traineddata")
			if (!file.exists()) {
				dir.mkdirs()
				copyFile(assets)
			}
		}
		
		private fun copyFile(assets: AssetManager) {
			try {
				val `in` = assets.open("tessdata/eng.traineddata")
				val out = FileOutputStream(datapath + "/tessdata/" + "eng.traineddata")
				val buffer = ByteArray(1024)
				var read = `in`.read(buffer)
				while (read != -1) {
					out.write(buffer, 0, read)
					read = `in`.read(buffer)
				}
			} catch (e: Exception) {
				Log.d("mylog", "couldn't copy with the following error : " + e.toString())
			}
		}
		
		fun init(context: Context){
			val datapath = context.filesDir.absolutePath + "/tesseract/"
			val assets = context.assets
			
			initTrainingFile(datapath, assets)
			
			mTess.init(datapath, "eng")
			
			mTess.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_ONLY
			mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<")
			init = true
		}
		
		fun setWhiteList(whiteList: String){
			mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList)
		}
		
		fun setBlackList(blackList: String){
			mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList)
		}
		
		fun getOCRResult(bitmap: Bitmap): String {
			if(init) {
				mTess.setImage(bitmap)
				val output = mTess.utF8Text
				return output
			}
			else throw OcrServiceNotInitialisedException()
		}
		
		fun binarizeImage(bitmap: Bitmap): Bitmap{
			var pixImage = ReadFile.readBitmap(bitmap)
			pixImage = Enhance.unsharpMasking(pixImage)
			pixImage = Binarize.otsuAdaptiveThreshold(pixImage)
			return WriteFile.writeBitmap(pixImage)
		}
	}
}


