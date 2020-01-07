package co.id.distriboost.util

import android.graphics.Bitmap
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.*
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


object ImageHelper {
    fun createFilename(prefixs: String): String {
        return "FILE_" + prefixs + "_" + SimpleDateFormat("ddMMyyHHmmSS").format(Date())
    }
     fun saveToInternalStorage(context: Context, bitmapImage: Bitmap, prefixs: String): String {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        var filename = this.createFilename(prefixs)
        val mypath = File(directory, filename)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 1, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        Log.d("FILESAVED", directory.absolutePath +  "/" + filename)
        return  filename
    }

     fun loadImageFromStorage(context: Context, viewId: ImageView, path: String) {

        try {
            val cw = ContextWrapper(context)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val f = File(directory.absolutePath +  "/" +  path)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            viewId.setImageBitmap(b)
        } catch (e: FileNotFoundException) {

        }

    }


    fun getImageFile(context: Context,  path: String) : File{

        try {
            val cw = ContextWrapper(context)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val f = File(directory.absolutePath +  "/" +  path)
            return  f
        } catch (e: FileNotFoundException) {

        }
        return  null!!;

    }
}