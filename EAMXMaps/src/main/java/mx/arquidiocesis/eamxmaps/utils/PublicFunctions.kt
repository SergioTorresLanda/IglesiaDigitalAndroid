package mx.arquidiocesis.eamxmaps.utils

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxmaps.repository.Repository
import java.io.File

class PublicFunctionsMaps {

     fun descargaImagen(context: Context, ruta: String, nombre: String) {
        val file=imagenInterna(context,nombre)
         if(!file.exists()){
             GlobalScope.launch {
                 Repository(context).descargarImagen( ruta, file)
             }
         }
    }

     fun updateImage(context: Context, ruta: String, nombre: String, listener : (Boolean) -> Unit) {
         val file = imagenInterna(context,nombre)
         if(file.exists()){
             val result = file.deleteRecursively()
             if(result) {
                 val newFile = file.createNewFile()
             }
         }

         GlobalScope.launch {
             Repository(context).updateImage(ruta, file) { result ->
                 listener(result)
             }
         }
    }

    fun imagenInterna(context: Context, nombre: String): File {
        return Repository(context).imagenInterna(nombre)
    }

}
