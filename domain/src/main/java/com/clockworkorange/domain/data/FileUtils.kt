package com.clockworkorange.domain.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun compressImageFromContentUri(context: Context, contentUri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(contentUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val imageFile = File(context.cacheDir, "tmp.jpg").apply {
            deleteOnExit()
            createNewFile()
        }
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)

        with(FileOutputStream(imageFile)){
            write(bos.toByteArray())
            flush()
            close()
        }
        return imageFile
    }

    fun copyFileFromContentUri(context: Context, contentUri: Uri, fileName: String): File{
        val inputStream = context.contentResolver.openInputStream(contentUri)
        val tmpFile = File(context.cacheDir, fileName).apply {
            deleteOnExit()
            createNewFile()
        }

        val outputStream = tmpFile.outputStream()
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return tmpFile
    }

}