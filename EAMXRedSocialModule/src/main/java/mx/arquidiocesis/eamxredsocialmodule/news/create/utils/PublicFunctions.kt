package mx.arquidiocesis.eamxredsocialmodule.news.create.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PublicFunctionsMaps {

   /* fun descargaImagen(context: Context, ruta: String, nombre: String) {
        val file = imagenInterna(context, nombre)
        if (!file.exists()) {
            GlobalScope.launch {
                Repository(context).descargarImagen(ruta, file)
            }
        }
    }*/

    fun updateImage(context: Context, ruta: String, nombre: String, listener: (Boolean) -> Unit) {
        val file = imagenInterna(context, nombre)
        if (file.exists()) {
            val result = file.deleteRecursively()
            if (result) {
                val newFile = file.createNewFile()
            }
        }

        GlobalScope.launch {
            Glide.with(context)
                .asBitmap()
                .load(Uri.parse(ruta))
                .override(300, 200)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val dest = file
                        try {
                            val out = FileOutputStream(dest)
                            resource.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            out.flush()
                            out.close()
                            listener(true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            listener(false)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }
    }

    fun imagenInterna(context: Context, nombre: String): File {
        val folder = File(context.applicationContext.filesDir.toString() + "/images/")
        folder.mkdir()
        return File(folder, "$nombre.jpg")
    }

}
