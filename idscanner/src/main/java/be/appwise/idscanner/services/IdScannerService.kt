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

/**
 * This service uses Tesseract to process images.
 * It needs to be initialised with IdScannerService.init(ApplicationContext) before use
 */
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
		
		
		/**
		 * Initialises Tesseract and links to its trained-data
		 * @param context ApplicationContext
		 */
		fun init(context: Context){
			val datapath = context.filesDir.absolutePath + "/tesseract/"
			val assets = context.assets
			
			initTrainingFile(datapath, assets)
			
			mTess.init(datapath, "eng")
			
			mTess.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_ONLY
			mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<")
			init = true
		}
		
		/**
		 * Sets whitelist. When this is set, Tesseract only searches for these characters.
		 * At default, this is set to ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789< to match the ID-card whitelist
		 * @param whiteList
		 */
		fun setWhiteList(whiteList: String){
			mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList)
		}
		
		/**
		 * Sets blacklist. When this is set, Tesseract ignores these characters when searching.
		 * At default, this is not set
		 * @param blackList
		 */
		fun setBlackList(blackList: String){
			mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList)
		}
		
		/**
		 * Uses Tesseract to search for text in an image bitmap
		 * @param bitmap image
		 * @return text found in bitmap
		 * @throws OcrServiceNotInitialisedException when Tesseract is not yet initialised
		 */
		fun getOCRResult(bitmap: Bitmap): String {
			if(init) {
				mTess.setImage(bitmap)
				val output = mTess.utF8Text
				return output
			}
			else throw OcrServiceNotInitialisedException()
		}
		
		/**
		 * Transform image to a binarized image so it is easier to be processed by Tesseract
		 * @param bitmap image
		 * @return binarized image
		 */
		fun binarizeImage(bitmap: Bitmap): Bitmap{
			var pixImage = ReadFile.readBitmap(bitmap)
			pixImage = Enhance.unsharpMasking(pixImage)
			pixImage = Binarize.otsuAdaptiveThreshold(pixImage)
			return WriteFile.writeBitmap(pixImage)
		}
	}
}


