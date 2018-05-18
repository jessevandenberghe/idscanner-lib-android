package be.appwise.idscanner.helpers

import android.graphics.*
import android.util.Log
import com.googlecode.leptonica.android.ReadFile
import com.googlecode.leptonica.android.Rotate
import com.googlecode.leptonica.android.WriteFile
import com.otaliastudios.cameraview.Frame
import java.io.ByteArrayOutputStream


private val tag = "bytearrayConverter"

fun convertYuvToBitmap(yuvData: ByteArray, frame: Frame): Bitmap? {
	val width = frame.size.width
	val height = frame.size.height
	val yuvImage = YuvImage(yuvData, ImageFormat.NV21, width, height, null)
	val stream = ByteArrayOutputStream()
	val quality = 80
	var decodedBitmap: Bitmap? = null
	
	try {
		yuvImage.compressToJpeg(Rect(0, 0, width, height), quality, stream)
		decodedBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)
	}
	catch (e: Exception){
		Log.e(tag, "convertYuvToBitmap: " , e)
	}
	
	return scaleBitmap(rotateBitmap(decodedBitmap))
}

fun rotateBitmap(bitmap: Bitmap?): Bitmap{
	
	var pixImage = ReadFile.readBitmap(bitmap)
	pixImage = Rotate.rotate(pixImage, 90f)
	return WriteFile.writeBitmap(pixImage)
	
}

fun scaleBitmap(bitmap: Bitmap?): Bitmap{
	return Bitmap.createBitmap(bitmap!!, 290, 0, bitmap.width - 580, bitmap.height - 43)
}