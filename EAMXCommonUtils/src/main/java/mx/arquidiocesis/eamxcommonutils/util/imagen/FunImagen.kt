package mx.arquidiocesis.eamxcommonutils.util.imagen

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class FunImagen {
    fun descargaImagen(context: Context, ruta: String, nombre: String) {
        val file=imagenInterna(context,nombre)
        if(!file.exists()){
            GlobalScope.launch {
                RespositoryImg(context).descargarImagen( ruta, file)
            }
        }
    }
    fun setFile(context: Context,resource: Bitmap, file: File){
        GlobalScope.launch {
            RespositoryImg(context).setFile(resource, file)
        }
    }

    fun updateImage(context: Context, ruta: String, nombre: String, listener : (Boolean) -> Unit) {
        val file = imagenInterna(context,nombre)
        if (file.exists()) file.deleteRecursively()
        GlobalScope.launch {
            RespositoryImg(context).updateImage(ruta, file) { result ->
                listener(result)
            }
        }
    }

    fun imagenInterna(context: Context, nombre: String): File {
        return RespositoryImg(context).imagenInterna(nombre)
    }
}