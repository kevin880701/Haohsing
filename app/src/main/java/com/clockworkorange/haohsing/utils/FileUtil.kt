package com.clockworkorange.haohsing.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    fun writeBitmapToFile(context: Context, bitmap: Bitmap, fileName: String = "tmp.jpg"): File {
        val file = File(context.cacheDir, fileName).apply {
            deleteOnExit()
            createNewFile()
        }

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)

        with(FileOutputStream(file)) {
            write(bos.toByteArray())
            flush()
            close()
        }

        return file
    }
}